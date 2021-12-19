package com.neige_i.go4lunch.view.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ActivitySettingsBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Init binding
        final ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        // Setup UI
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setTitle(R.string.settings_menu);

        binding.notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.onNotificationSwitchCheckChanged(isChecked);
        });

        // Update UI when state is changed
        viewModel.getViewState().observe(this, isMiddayNotificationEnabled -> {
            binding.notificationSwitch.setChecked(isMiddayNotificationEnabled);
        });

        // Update UI when events are triggered
        viewModel.getCloseActivityEvent().observe(this, unused -> {
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        viewModel.onOptionsItemSelected(item.getItemId());
        return false;
    }
}