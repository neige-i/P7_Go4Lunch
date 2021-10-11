package com.neige_i.go4lunch.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ActivityAuthBinding;
import com.neige_i.go4lunch.view.home.HomeActivity;

import java.util.Arrays;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    // -------------------------------------- LOCAL FIELDS --------------------------------------

    private AuthViewModel viewModel;
    @NonNull
    private final CallbackManager facebookCallbackManager = CallbackManager.Factory.create();

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Init view binding
        final ActivityAuthBinding binding = ActivityAuthBinding.inflate(getLayoutInflater());

        // Setup activity result callback
        final ActivityResultLauncher<Intent> signInActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), ignored -> {
                // The Facebook login SDK doesn't use the Activity Result API yet
                // That is why the callback result is ignored and is handled in onActivityResult()
            });

        // Setup Firebase sign-in methods
        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
            this,
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // IMPORTANT
                .requestEmail()
                .build()
        );
        final LoginManager loginManager = LoginManager.getInstance();
        handleFacebookLogin(loginManager);

        // Setup UI
        setContentView(binding.getRoot());

        binding.googleSignInBtn.setOnClickListener(
            v -> signInActivityResultLauncher.launch(googleSignInClient.getSignInIntent())
        );

        binding.facebookSignInBtn.setOnClickListener(
            v -> loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        );

        // Update UI when state is changed
        viewModel.getAuthViewState().observe(this, authViewState -> {
            binding.progressBar.setVisibility(authViewState.isProgressBarVisible() ? View.VISIBLE : View.GONE);
            binding.googleSignInBtn.setEnabled(authViewState.isButtonEnabled());
            binding.facebookSignInBtn.setEnabled(authViewState.isButtonEnabled());
        });

        // Setup actions when events are triggered
        viewModel.getStartHomeActivityEvent().observe(this, unused -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        viewModel.getShowErrorEvent().observe(this, stringId ->
            Snackbar.make(binding.getRoot(), stringId, Snackbar.LENGTH_INDEFINITE).show()
        );
    }

    // ------------------------------------ NAVIGATION METHODS -------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            // Pass the activity result back to the Facebook SDK
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            handleGoogleLogin(data);
        }
    }

    // --------------------------------------- LOGIN METHODS ---------------------------------------

    private void handleGoogleLogin(@Nullable Intent googleIntent) {
        try {
            final String idToken = GoogleSignIn.getSignedInAccountFromIntent(googleIntent)
                .getResult(ApiException.class)
                .getIdToken();
            viewModel.signInToFirebase(GoogleAuthProvider.getCredential(idToken, null));
        } catch (ApiException e) {
            viewModel.handleGoogleSignInError(e);
        }
    }

    private void handleFacebookLogin(@NonNull LoginManager loginManager) {
        loginManager.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                viewModel.signInToFirebase(FacebookAuthProvider.getCredential(accessToken.getToken()));
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                viewModel.handleFacebookSignInError(error);
            }
        });
    }
}