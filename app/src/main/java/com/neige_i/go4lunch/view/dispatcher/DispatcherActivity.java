package com.neige_i.go4lunch.view.dispatcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.view.auth.AuthActivity;
import com.neige_i.go4lunch.view.home.HomeActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DispatcherActivity extends AppCompatActivity {

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dispatcher does not set content view

        // Init ViewModel and update UI when events are triggered
        new ViewModelProvider(this).get(DispatcherViewModel.class)
            .getStartActivityEvent()
            .observe(this, activityToStart -> {
                switch (activityToStart) {
                    case HOME_ACTIVITY:
                        redirectTo(HomeActivity.class);
                        break;
                    case AUTH_ACTIVITY:
                        redirectTo(AuthActivity.class);
                        break;
                }
            });
    }

    private void redirectTo(@NonNull Class<? extends AppCompatActivity> activity) {
        startActivity(new Intent(this, activity));
        finish();
    }
}