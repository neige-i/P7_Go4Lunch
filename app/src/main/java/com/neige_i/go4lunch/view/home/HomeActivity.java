package com.neige_i.go4lunch.view.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.detail.DetailActivity;
import com.neige_i.go4lunch.view.list_restaurant.RestaurantListFragment;
import com.neige_i.go4lunch.view.list_workmate.WorkmateListFragment;
import com.neige_i.go4lunch.view.map.MapFragment;
import com.neige_i.go4lunch.view.util.OnDetailsQueriedCallback;
import com.neige_i.go4lunch.view.util.ViewModelFactory;

public class HomeActivity extends AppCompatActivity implements OnDetailsQueriedCallback {

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    public static final String PLACE_ID_INTENT_EXTRA = BuildConfig.APPLICATION_ID + "placeId";

    static final String MAP_FRAGMENT_TAG = "map";
    static final String RESTAURANT_FRAGMENT_TAG = "restaurant";
    static final String WORKMATE_FRAGMENT_TAG = "workmate";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    private HomeViewModel viewModel;

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config layout and toolbar
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Init ViewModel
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(HomeViewModel.class);

        // Config BottomNavigationView listener
        ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setOnNavigationItemSelectedListener(item -> {
            viewModel.onNavigationItemSelected(item.getItemId());
            return true;
        });

        // Update UI when state is changed
        viewModel.getTitleIdState().observe(this, this::setTitle);

        // Config actions when events are triggered
        viewModel.getRequestLocationPermissionEvent().observe(this, aBoolean ->
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
            )
        );
        configFragmentTransactions(getSupportFragmentManager());
    }

    private void configFragmentTransactions(@NonNull FragmentManager fragmentManager) {
        viewModel.getHideFragmentEvent().observe(this, fragmentToHideTag -> {
            // This LiveData contains the tag of the currently displayed fragment, findFragmentByTag() should never return null
            final Fragment fragmentToHide = fragmentManager.findFragmentByTag(fragmentToHideTag);
            assert fragmentToHide != null;

            fragmentManager.beginTransaction().hide(fragmentToHide).commit();
        });

        viewModel.getShowFragmentEvent().observe(this, fragmentToShowTag -> {
            // This LiveData contains the tag of a fragment that has already been displayed, findFragmentByTag() should never return null
            final Fragment fragmentToShow = fragmentManager.findFragmentByTag(fragmentToShowTag);
            assert fragmentToShow != null;

            fragmentManager.beginTransaction().show(fragmentToShow).commit();
        });

        viewModel.getAddMapFragmentEvent().observe(this, aBoolean ->
            addFragment(fragmentManager, new MapFragment(), MAP_FRAGMENT_TAG));

        viewModel.getAddRestaurantFragmentEvent().observe(this, aBoolean ->
            addFragment(fragmentManager, RestaurantListFragment.newInstance(), RESTAURANT_FRAGMENT_TAG));

        viewModel.getAddWorkmateFragmentEvent().observe(this, aBoolean ->
            addFragment(fragmentManager, WorkmateListFragment.newInstance(), WORKMATE_FRAGMENT_TAG));
    }

    private void addFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull String tag) {
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check location permission here in case the user manually changes it outside the app
        viewModel.updateLocationPermission(
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.removeLocationUpdates();
    }

    // ------------------------------------- PERMISSION METHODS ------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        viewModel.updateLocationPermission(
            requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
        );
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
        startActivity(new Intent(this, DetailActivity.class).putExtra(PLACE_ID_INTENT_EXTRA, placeId));
    }
}