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

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    @NonNull
    private final FusedLocationProviderClient fusedLocationClient;
    @NonNull
    private final LocationCallback locationCallback;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public LocationRepositoryImpl() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainApplication.getInstance());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                final Location lastLocation = locationResult.getLastLocation();
                Log.d("Neige", "LocationRepository::onLocationResult: last location: " + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
                currentLocation.setValue(lastLocation);
            }
        };
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

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
        Log.d("Neige", "LocationRepositoryImpl::removeLocationUpdates");
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
