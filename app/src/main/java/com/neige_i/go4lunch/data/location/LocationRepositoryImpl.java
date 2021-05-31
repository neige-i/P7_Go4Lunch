package com.neige_i.go4lunch.data.location;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.neige_i.go4lunch.MainApplication;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;

import java.util.Objects;

public class LocationRepositoryImpl implements LocationRepository {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<Location> currentLocationMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MediatorSingleLiveEvent<ResolvableApiException> enableGpsEvent = new MediatorSingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Provider to start and stop location updates.
     */
    @NonNull
    private final FusedLocationProviderClient fusedLocationProviderClient = LocationServices
        .getFusedLocationProviderClient(MainApplication.getInstance());
    /**
     * Data object that determines how location should be requested.
     */
    @NonNull
    private final LocationRequest locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(5000)
        .setFastestInterval(5000)
        .setSmallestDisplacement(10);
    /**
     * Callback triggered when location-related updates occur
     */
    @NonNull
    private final LocationCallback locationCallback;
    /**
     * Flag used to start requesting location only if it is not already requested.
     */
    private boolean areLocationUpdatesRequested;

    /**
     * The {@link ResolvableApiException} is used to show the dialog that prompt the user to enable GPS.
     */
    @NonNull
    private final MutableLiveData<ResolvableApiException> resolvableApiExceptionMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> requestGpsMutableLiveData = new MutableLiveData<>(true);

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public LocationRepositoryImpl() {
        final SettingsClient settingsClient = LocationServices.getSettingsClient(MainApplication.getInstance());
        final LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build();

        locationCallback = new LocationCallback() {
            // Update the current location when a new one is available
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                final Location lastLocation = locationResult.getLastLocation();
                Log.d("Neige", "REPO lastLocation: " + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
                currentLocationMutableLiveData.setValue(lastLocation);
            }

            // Set and reset the ResolvableApiException object when the GPS settings change
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {
                        Log.d("Neige", "REPO gpsAvailable: yes");
                        resolvableApiExceptionMutableLiveData.setValue(null);
                        requestGpsMutableLiveData.setValue(false); // No need to request GPS if already enabled
                    })
                    .addOnFailureListener(e -> {
                        if (((ApiException) e).getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            Log.d("Neige", "REPO gpsAvailable: no");
                            resolvableApiExceptionMutableLiveData.setValue((ResolvableApiException) e);
                        }
                    });
            }
        };

        // Set when to prompt the user to enable GPS
        enableGpsEvent.addSource(resolvableApiExceptionMutableLiveData, resolvableApiException ->
            combine(resolvableApiException, requestGpsMutableLiveData.getValue())
        );
        enableGpsEvent.addSource(requestGpsMutableLiveData, isGpsRequested ->
            combine(resolvableApiExceptionMutableLiveData.getValue(), isGpsRequested)
        );
    }

    /**
     * Sets when to prompt the user to enable GPS.<br />
     * The {@code ResolvableApiException} argument is automatically updated each time the GPS is switched on or off.
     * But, it would be too intrusive if the user is prompted to enable the GPS each time he disables it.
     * The {@code isGpsNeeded} flag is used to ask the user to turn on the GPS only when necessary.
     */
    private void combine(@Nullable ResolvableApiException resolvableApiException, @Nullable Boolean isGpsRequested) {
        if (resolvableApiException != null && Objects.equals(isGpsRequested, true)) {
            // Prompt the user to enable GPS
            Log.d("Neige", "REPO requestGps");
            enableGpsEvent.setValue(resolvableApiException);

            // Reset flag
            requestGpsMutableLiveData.setValue(false);
        }
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
        Log.d("Neige", "REPO removeLocationUpdates");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        areLocationUpdatesRequested = false;
    }

    @NonNull
    @Override
    public LiveData<ResolvableApiException> getEnableGpsEvent() {
        return enableGpsEvent;
    }

    @NonNull
    @Override
    public LiveData<Boolean> isGpsEnabled() {
        return Transformations.map(resolvableApiExceptionMutableLiveData, Objects::isNull);
    }

    @Override
    public void requestGps() {
        requestGpsMutableLiveData.setValue(true);
    }
}
