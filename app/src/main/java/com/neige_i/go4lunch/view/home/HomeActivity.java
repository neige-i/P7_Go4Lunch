package com.neige_i.go4lunch.view.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.databinding.ActivityHomeBinding;
import com.neige_i.go4lunch.view.StartDetailActivityCallback;
import com.neige_i.go4lunch.view.detail.DetailActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity implements StartDetailActivityCallback {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    public static final String EXTRA_PLACE_ID = BuildConfig.APPLICATION_ID + ".placeId";

    // ---------------------------------------- LOCAL FIELDS ---------------------------------------

    private HomeViewModel viewModel;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    @Inject
    GpsStateChangeReceiver gpsStateChangeReceiver;

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Init view binding
        final ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());

        // Setup UI
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.viewPager.setUserInputEnabled(false); // Disable page scrolling because the ViewPager contains a scrollable map
        binding.viewPager.setAdapter(new HomePagerAdapter(this));

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            viewModel.onBottomNavigationItemClicked(item.getItemId());
            return true;
        });

        // Setup activity result callbacks
        final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), ignored -> {
                // The location permission is already checked in onResume()
                // Which is called just after the permission dialog being dismissed
            });
        final ActivityResultLauncher<IntentSenderRequest> showGpsDialogLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), ignored -> {
                // The GPS status result is already retrieved in the receiver
            });

        // Update UI when state is changed
        viewModel.getHomeViewState().observe(this, homeViewState -> {
            binding.toolbar.setTitle(homeViewState.getTitleId());
            binding.viewPager.setCurrentItem(homeViewState.getViewPagerPosition());

            searchMenuItem.setVisible(homeViewState.isSearchEnabled());
        });

        // Setup actions when events are triggered
        viewModel.getRequestLocationPermissionEvent().observe(this, unused -> {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        });
        viewModel.getShowGpsDialogEvent().observe(this, resolvableApiException -> {
            showGpsDialogLauncher.launch(
                new IntentSenderRequest.Builder(resolvableApiException.getResolution()).build()
            );
        });
        viewModel.getShowBlockingDialogEvent().observe(this, unused -> {
            new AlertDialog.Builder(this)
                .setTitle(R.string.mandatory_permission_title)
                .setMessage(R.string.mandatory_permission_message)
                .setPositiveButton(R.string.positive_button_text, (dialog, which) -> {
                    startActivity(new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null)
                    ));
                })
                .setNegativeButton(R.string.negative_button_text, (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
        });
        viewModel.getCollapseSearchViewEvent().observe(this, unused -> {
            searchMenuItem.collapseActionView();
        });
        viewModel.getExpandSearchViewEvent().observe(this, searchQuery -> {
            searchMenuItem.expandActionView();
            searchView.setQuery(searchQuery, false); // false to only update text field
        });

        // Register GPS receiver in this activity's lifecycle
        registerReceiver(
            gpsStateChangeReceiver,
            new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check location permission here in case the user manually changes it outside the app
        viewModel.onActivityResumed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(gpsStateChangeReceiver);
    }

    // ------------------------------------ OPTIONS MENU METHODS -----------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        viewModel.onSearchMenuItemInitialized();

        // Setup SearchView
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_restaurants_by_name));
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE); // Close keyboard when clicking on "Return" key
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.onQueryTextChange(newText);
                return false;
            }
        });
        // searchView.setOnCloseListener() does not work
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d("Neige", "onMenuItemActionExpand: ");
                viewModel.onSearchMenuExpanded();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d("Neige", "onMenuItemActionCollapse: ");
                viewModel.onSearchMenuCollapsed();
                return true;
            }
        });
        return true;
    }

    // ------------------------------------- CALLBACK METHODS --------------------------------------

    @Override
    public void showDetailedInfo(@NonNull String placeId) {
        startActivity(new Intent(this, DetailActivity.class).putExtra(EXTRA_PLACE_ID, placeId));
    }
}