package com.neige_i.go4lunch.view.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.detail.DetailActivity;
import com.neige_i.go4lunch.view.list.ListFragment;
import com.neige_i.go4lunch.view.map.MapFragment;
import com.neige_i.go4lunch.view.util.OnDetailsQueriedCallback;
import com.neige_i.go4lunch.view.util.ViewModelFactory;

public class HomeActivity extends AppCompatActivity implements OnDetailsQueriedCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    static final String TAG_FRAGMENT_MAP = "map";
    static final String TAG_FRAGMENT_RESTAURANT = "restaurant";
    static final String TAG_FRAGMENT_WORKMATE = "workmate";

    public static final String EXTRA_PLACE_ID = "EXTRA_PLACE_ID";

    private HomeViewModel viewModel;

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
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Update UI when state is changed
        viewModel.getViewState().observe(this, viewState -> {
            final Fragment fragmentToShow = fragmentManager.findFragmentByTag(viewState.getFragmentToShow());
            final Fragment fragmentToHide = fragmentManager.findFragmentByTag(viewState.getFragmentToHide());

            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // 1. Hide the existing fragment
            if (fragmentToHide != null)
                fragmentTransaction.hide(fragmentToHide);

            if (fragmentToShow != null) {
                // 2.a. Show the existing fragment
                fragmentTransaction.show(fragmentToShow);
            } else {
                // 2.b. Add the new fragment
                final Fragment fragmentToAdd;
                switch (viewState.getFragmentToShow()) {
                    case TAG_FRAGMENT_MAP:
                        fragmentToAdd = new MapFragment();
                        break;
                    case TAG_FRAGMENT_RESTAURANT:
                        fragmentToAdd = ListFragment.newInstance(ListFragment.RESTAURANT);
                        break;
                    case TAG_FRAGMENT_WORKMATE:
                        fragmentToAdd = ListFragment.newInstance(ListFragment.WORKMATE);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + viewState.getFragmentToShow());
                }
                fragmentTransaction.add(R.id.fragment_container, fragmentToAdd, viewState.getFragmentToShow());
            }

            // 3. Commit the fragment transaction
            fragmentTransaction.commit();

            // Update the toolbar title accordingly
            setTitle(viewState.getTitleId());
        });

        viewModel.getRequestLocationPermissionEvent().observe(this, aVoid -> {
            Log.d("Neige", "HomeActivity::observe request location permission");
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
            );
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check location permission here in case the user manually changes it outside the app
        viewModel.onLocationPermissionChecked();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.onLocationUpdatesRemoved();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO: fix infinite loop if deny permission
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

    @Override
    public void onDetailsQueried(@NonNull String placeId) {
        final Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, placeId);
        startActivity(intent);
    }
}