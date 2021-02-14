package com.neige_i.go4lunch.data.location;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.neige_i.go4lunch.MainApplication;

public class LocationRepositoryImpl implements LocationRepository {

    @NonNull
    private final MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    @NonNull
    private final FusedLocationProviderClient fusedLocationClient;
    @NonNull
    private final LocationCallback locationCallback;

    public LocationRepositoryImpl() {
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
    @Override
    public LiveData<Boolean> getLocationPermission() {
        return locationPermission;
    }

    @Override
    public void updateLocationPermission(boolean isPermissionGranted) {
        locationPermission.setValue(isPermissionGranted);
    }

    @NonNull
    @Override
    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startLocationUpdates() {
        Log.d("Neige", "LocationRepository::startLocationUpdates");
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

    @Override
    public void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
