package com.neige_i.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setOnNavigationItemSelectedListener(item -> {
            final int id = item.getItemId();
            final String whichScreen;
            if (id == R.id.action_map) {
                whichScreen = "map";
            } else if (id == R.id.action_list) {
                whichScreen = "list";
            } else if (id == R.id.action_workmates) {
                whichScreen = "workmates";
            } else {
                whichScreen = "null";
            }
            Log.d("Neige", "MainActivity::onNavigationItemSelected: " + whichScreen);
            return true;
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
            Log.d("Neige", "MainActivity::onOptionsItemSelected: search is clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}