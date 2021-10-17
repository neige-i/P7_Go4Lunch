package com.neige_i.go4lunch.data.location;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationRepositoryImpl implements LocationRepository {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    /**
     * Provider to start and stop location updates.
     */
    @NonNull
    private final FusedLocationProviderClient fusedLocationProviderClient;
    /**
     * Settings to retrieve the Google Services GPS dialog.
     */
    @NonNull
    private final SettingsClient settingsClient;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<Location> currentLocationMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<ResolvableApiException> gpsDialogMutableLiveData = new MutableLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Determines how location should be requested.
     */
    @NonNull
    private final LocationRequest locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(5000)
        .setFastestInterval(5000)
        .setSmallestDisplacement(10);
    /**
     * Callback triggered when location is updated.
     */
    @Nullable
    private LocationCallback locationCallback;
    /**
     * Location Settings to be checked. Used to prompt the Google Services GPS dialog to the user.
     */
    @NonNull
    private final LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true)
        .build();
    /**
     * Flag used to start requesting location only if it is not already requested.
     */
    private boolean areLocationUpdatesRequested;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public LocationRepositoryImpl(
        @NonNull FusedLocationProviderClient fusedLocationProviderClient,
        @NonNull SettingsClient settingsClient
    ) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.settingsClient = settingsClient;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<Location> getCurrentLocation() {
        return currentLocationMutableLiveData;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startLocationUpdates() {
        // Init location callback
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    final Location lastLocation = locationResult.getLastLocation();
                    Log.d("Neige", "REPO lastLocation: " + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
                    currentLocationMutableLiveData.setValue(lastLocation);
                }
            };
        }

        if (!areLocationUpdatesRequested) {
            Log.d("Neige", "REPO startLocationUpdates");
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            );
            areLocationUpdatesRequested = true;
        }
    }

    @Override
    public void removeLocationUpdates() {
        if (locationCallback != null) {
            Log.d("Neige", "REPO removeLocationUpdates");
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            areLocationUpdatesRequested = false;
        }
    }

    @Override
    public boolean areLocationUpdatesNeverStarted() {
        return locationCallback == null;
    }

    @NonNull
    @Override
    public LiveData<ResolvableApiException> getGpsDialog() {
        return gpsDialogMutableLiveData;
    }

    @Override
    public void requestGpsDialog() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnFailureListener(e -> {
                final ResolvableApiException exception = (ResolvableApiException) e;
                if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    Log.d("Neige", "REPO promptGpsDialog");
                    gpsDialogMutableLiveData.setValue(exception);
                }
            });
    }
}
