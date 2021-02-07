package com.neige_i.go4lunch.data.google_places;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.neige_i.go4lunch.MainApplication;

public class LocationRepository {

    @NonNull
    private final MutableLiveData<Boolean> isLocationPermissionGranted = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    @NonNull
    private final FusedLocationProviderClient fusedLocationClient;
    @NonNull
    private final LocationCallback locationCallback;

    public LocationRepository() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainApplication.getInstance());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    final Location lastLocation = locationResult.getLastLocation();
                    if (lastLocation != null) {
                        Log.d("Neige", "LocationRepository::onLocationResult: update " + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
                        // Update current user location
                        currentLocation.setValue(lastLocation);
                    }
                }
            }
        };
    }

    @NonNull
    public LiveData<Boolean> isLocationPermissionGranted() {
        return isLocationPermissionGranted;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        isLocationPermissionGranted.setValue(locationPermissionGranted);

        if (locationPermissionGranted)
            startLocationUpdates();
    }

    @NonNull
    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public void checkLocationPermission() {
        setLocationPermissionGranted(
            ContextCompat.checkSelfPermission(
                MainApplication.getInstance(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        );
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
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
    }

    public void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
