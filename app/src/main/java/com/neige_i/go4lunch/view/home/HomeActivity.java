package com.neige_i.go4lunch.view.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.ViewModelFactory;
import com.neige_i.go4lunch.view.list.ListFragment;
import com.neige_i.go4lunch.view.map.MapFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    static final String TAG_FRAGMENT_MAP = "map";
    static final String TAG_FRAGMENT_RESTAURANT = "restaurant";
    static final String TAG_FRAGMENT_WORKMATE = "workmate";

    private HomeViewModel viewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @SuppressLint("MissingPermission")
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
            viewModel.onFragmentSelected(item.getItemId());
            return true;
        });

        // Init fragment manager
        final FragmentManager fragmentManager = initFragmentManager();

        // Update UI when state is changed
        viewModel.getUiState().observe(this, homeUiModel -> {
            // Show the correct fragment
            fragmentManager.beginTransaction()
                .hide(fragmentManager.findFragmentByTag(homeUiModel.getFragmentToHide()))
                .show(fragmentManager.findFragmentByTag(homeUiModel.getFragmentToShow()))
                .commit();
            viewModel.setFragmentToHide(homeUiModel.getFragmentToShow());

            // Update the toolbar title accordingly
            setTitle(homeUiModel.getTitleId());
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                viewModel.onCurrentLocationUpdated(locationResult);
            }
        };
        viewModel.getStartLocationUpdatesEvent().observe(this, aVoid -> {
            // ASKME: is last known location really useful
//            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
//                if (location != null) {
//
//                }
//            });

            // ASKME: com.google.android.gms.location better than android.location
//            ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                5000,
//                10,
//                location -> {}
//            );

            // TODO: handle when gps is disabled
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(5000)
                    .setSmallestDisplacement(10),
                locationCallback,
                Looper.getMainLooper()
            );
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Request location permission if not granted yet
        // If the user manually disables permission in device settings, the activity is recreated
        // But, if the user enables it, the activity is only restarted
        // Then, to better handle actions according to location permission, check it here inside onStart()
        // ASKME: can keep if statement here or replace it by SingleLiveEvent
        final boolean isLocationPermissionGranted;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            isLocationPermissionGranted = false;
        } else {
            isLocationPermissionGranted = true;
        }
        viewModel.onLocationPermissionGranted(isLocationPermissionGranted);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        viewModel.onLocationPermissionGranted(requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                                                  grantResults.length > 0 &&
                                                  grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Log.d("Neige", "HomeActivity::onOptionsItemSelected: search item");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private FragmentManager initFragmentManager() {
        // Init BottomNavigationView's fragments
        final Map<String, Fragment> fragments = new HashMap<>();
        fragments.put(TAG_FRAGMENT_MAP, new MapFragment());
        fragments.put(TAG_FRAGMENT_RESTAURANT, ListFragment.newInstance(ListFragment.RESTAURANT));
        fragments.put(TAG_FRAGMENT_WORKMATE, ListFragment.newInstance(ListFragment.WORKMATE));

        // Add all fragments to the fragment manager and hide them
        // TODO: add restaurant and workmate fragments only when clicked on the BottomNavigationView to avoid unnecessary initializations
        final FragmentManager fragmentManager = getSupportFragmentManager();
        for (Map.Entry<String, Fragment> mapEntry : fragments.entrySet()) {
            fragmentManager.beginTransaction()
                .add(R.id.fragment_container, mapEntry.getValue(), mapEntry.getKey())
                .hide(mapEntry.getValue())
                .commit();
        }

        return fragmentManager;
    }
}