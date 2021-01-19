package com.neige_i.go4lunch.view.home;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.ViewModelFactory;
import com.neige_i.go4lunch.view.list.ListFragment;
import com.neige_i.go4lunch.view.map.MapFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    static final String TAG_FRAGMENT_MAP = "map";
    static final String TAG_FRAGMENT_RESTAURANT = "restaurant";
    static final String TAG_FRAGMENT_WORKMATE = "workmate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config layout and toolbar
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Init ViewModel
        final HomeViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(HomeViewModel.class);

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