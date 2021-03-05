package com.neige_i.go4lunch.view.dispatcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.view.util.ViewModelFactory;

public class DispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dispatcher does not set content view

        // 1. Init ViewModel
        final DispatcherViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(DispatcherViewModel.class);

        // 2. Let the ViewModel handle the logic
        viewModel.onSignedInUserChecked();

        // 3. Observe when the ViewModel has computed which activity to start
        viewModel.getStartActivityEvent().observe(this, classToStart -> {
            startActivity(new Intent(this, classToStart));

            // Finish this activity just after starting the new one
            // If user presses the back button, he won't be redirected to this activity
            finish();
        });
    }
}