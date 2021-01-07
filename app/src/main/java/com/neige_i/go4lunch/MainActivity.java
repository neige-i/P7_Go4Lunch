package com.neige_i.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config layout and toolbar
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Config which screen to show
        final BottomNavigationView bottomNavigationView = ((BottomNavigationView) findViewById(R.id.bottom_navigation));
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> showFragment(item.getItemId()));
        bottomNavigationView.setSelectedItemId(R.id.action_map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Log.d("Neige", "MainActivity::onOptionsItemSelected: search is clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean showFragment(int itemId) {
        final Fragment fragmentToShow;
        final int titleId;
        if (itemId == R.id.action_map) {
            fragmentToShow = new MapFragment();
            titleId = R.string.title_restaurant;
        } else if (itemId == R.id.action_list) {
            fragmentToShow = ListFragment.newInstance(ListFragment.RESTAURANT);
            titleId = R.string.title_restaurant;
        } else if (itemId == R.id.action_workmates) {
            fragmentToShow = ListFragment.newInstance(ListFragment.WORKMATE);
            titleId = R.string.title_workmates;
        } else {
            throw new IllegalStateException("Unexpected value: " + itemId);
        }

        // Show the correct fragment
        getSupportFragmentManager().beginTransaction()
            // ASKME: fragment navigation back stack, what happens if in workmate list and press back (exit or not?)
            .replace(R.id.fragment_container, fragmentToShow)
            .commit();

        // Update the toolbar title accordingly
        setTitle(titleId);

        return true;
    }
}