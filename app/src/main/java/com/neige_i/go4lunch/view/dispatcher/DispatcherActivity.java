package com.neige_i.go4lunch.view.dispatcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.view.auth.AuthActivity;
import com.neige_i.go4lunch.view.home.HomeActivity;
import com.neige_i.go4lunch.view.util.ViewModelFactory;

public class DispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dispatcher does not set content view

        // Observe when the ViewModel has computed which activity to start
        new ViewModelProvider(this, ViewModelFactory.getInstance())
            .get(DispatcherViewModel.class)
            .getStartActivityEvent()
            .observe(this, activityToStart -> {
                switch (activityToStart) {
                    case HOME_ACTIVITY:
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                        break;
                    case AUTH_ACTIVITY:
                        startActivity(new Intent(this, AuthActivity.class));
                        finish();
                        break;
                }
            });
    }
}