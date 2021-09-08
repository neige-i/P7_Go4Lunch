package com.neige_i.go4lunch.view.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ActivityMainBinding;
import com.neige_i.go4lunch.view.OnDetailsQueriedCallback;
import com.neige_i.go4lunch.view.ViewModelFactory;
import com.neige_i.go4lunch.view.detail.DetailActivity;

public class HomeActivity extends AppCompatActivity implements OnDetailsQueriedCallback {

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    public static final String EXTRA_PLACE_ID = BuildConfig.APPLICATION_ID + ".placeId";

    // ---------------------------------------- LOCAL FIELDS ---------------------------------------

    private HomeViewModel viewModel;

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init ViewModel
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(HomeViewModel.class);

        // Init view binding
        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Setup UI
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.viewPager.setUserInputEnabled(false); // Disable page scrolling because the ViewPager contains a scrollable map
        binding.viewPager.setAdapter(new HomePagerAdapter(this));

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            viewModel.setViewState(item.getItemId());
            return true;
        });

        // Setup activity result callbacks
        final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->
                viewModel.setLocationPermissionAndUpdates(isGranted));
        final ActivityResultLauncher<IntentSenderRequest> enableGpsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), ignored -> {
                // The GPS status result is already retrieved in the repository
            });

        // Update UI when state is changed
        viewModel.getHomeViewState().observe(this, homeViewState -> {
            setTitle(homeViewState.getTitleId());
            binding.viewPager.setCurrentItem(homeViewState.getViewPagerPosition());
        });

        // Setup actions when events are triggered
        viewModel.getRequestLocationPermissionEvent().observe(this, unused ->
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        );
        viewModel.getEnableGpsEvent().observe(this, resolvableApiException ->
            enableGpsLauncher.launch(new IntentSenderRequest.Builder(resolvableApiException.getResolution()).build())
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check location permission here in case the user manually changes it outside the app
        viewModel.setLocationPermissionAndUpdates(
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        );
    }

    @Override
    protected void onPause() {
        super.onPause();

        viewModel.stopLocationUpdates();
    }

    // ------------------------------------ OPTIONS MENU METHODS -----------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------- CALLBACK METHODS --------------------------------------

    @Override
    public void onDetailsQueried(@NonNull String placeId) {
        startActivity(new Intent(this, DetailActivity.class).putExtra(EXTRA_PLACE_ID, placeId));
    }
}