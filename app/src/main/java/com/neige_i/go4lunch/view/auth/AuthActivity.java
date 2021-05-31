package com.neige_i.go4lunch.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.home.HomeActivity;
import com.neige_i.go4lunch.view.util.ViewModelFactory;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 123;

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    private AuthViewModel viewModel;

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        final View constraintLayout = findViewById(R.id.constraint_layout);

        // Init ViewModel
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);

        // Configure Google sign-in
        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
            this,
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // IMPORTANT
                .requestEmail()
                .build()
        );

        findViewById(R.id.google_sign_in_btn).setOnClickListener(v -> startActivityForResult(
            googleSignInClient.getSignInIntent(),
            GOOGLE_SIGN_IN_REQUEST_CODE
        ));

        // Configure Facebook sign-in
        findViewById(R.id.facebook_sign_in_btn).setOnClickListener(v -> viewModel.onFacebookLoggedIn());

        viewModel.getFacebookLoginEvent().observe(this, loginManager -> loginManager.logInWithReadPermissions(
            this,
            Arrays.asList("email", "public_profile")
        ));

        // Observe ViewModel's event
        viewModel.getStartHomeActivityEvent().observe(this, aVoid -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        viewModel.getShowErrorEvent().observe(this, stringId ->
            Snackbar.make(constraintLayout, stringId, Snackbar.LENGTH_SHORT).show());
    }

    // ------------------------------------ NAVIGATION METHODS -------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onSignInResult(requestCode, resultCode, data);
    }
}