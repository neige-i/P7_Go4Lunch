package com.neige_i.go4lunch.data.location;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

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

public class LocationRepositoryImpl implements LocationRepository {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<Location> currentLocationMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MediatorLiveData<ResolvableApiException> gpsDialogPrompt = new MediatorLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Provider to start and stop location updates.
     */
    @NonNull
    private final FusedLocationProviderClient fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(MainApplication.getInstance());
    // TODO: change below docu
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
    private final MutableLiveData<ResolvableApiException> gpsDisabledMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> requestGpsPing = new MutableLiveData<>(true);

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public LocationRepositoryImpl() {
        // TODO: inject MainApplication instance
        final SettingsClient settingsClient = LocationServices.getSettingsClient(MainApplication.getInstance());
        final LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build();

        locationCallback = new LocationCallback() {
            // Update the current location
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                final Location lastLocation = locationResult.getLastLocation();
                Log.d("Neige", "REPO lastLocation: " + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
                currentLocationMutableLiveData.setValue(lastLocation);
            }

            // Update the GPS status
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {
                        Log.d("Neige", "REPO gpsAvailable: yes");
                        gpsDisabledMutableLiveData.setValue(null);
                        requestGpsPing.setValue(null); // No need to request GPS if already enabled
                    })
                    .addOnFailureListener(e -> {
                        final ResolvableApiException exception = (ResolvableApiException) e;
                        if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            Log.d("Neige", "REPO gpsAvailable: no");
                            gpsDisabledMutableLiveData.setValue(exception);
                        }
                    });
            }
        };

        // Set when to prompt the user to enable GPS
        gpsDialogPrompt.addSource(gpsDisabledMutableLiveData, resolvableApiException -> combine(resolvableApiException, requestGpsPing.getValue()));
        gpsDialogPrompt.addSource(requestGpsPing, isGpsRequested -> combine(gpsDisabledMutableLiveData.getValue(), isGpsRequested));
    }

    /**
     * Sets when to prompt the user to enable GPS.<br />
     * The {@code ResolvableApiException} argument is automatically updated each time the GPS is switched on or off.
     * But, it would be too intrusive if the user is prompted to enable the GPS each time he disables it.
     * The {@code isGpsNeeded} flag is used to ask the user to turn on the GPS only when necessary.
     */
    private void combine(@Nullable ResolvableApiException resolvableApiException, @Nullable Boolean isGpsRequested) {
        if (resolvableApiException == null || isGpsRequested == null) {
            return;
        }

        requestGpsPing.setValue(null); // Reset ping

        // Prompt the user to enable GPS only if it is disabled (when resolvableApiException != null)
        Log.d("Neige", "REPO promptGpsDialog");
        gpsDialogPrompt.setValue(resolvableApiException);
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
    public LiveData<ResolvableApiException> getGpsDialogPrompt() {
        return gpsDialogPrompt;
    }

    @NonNull
    @Override
    public LiveData<Boolean> isGpsEnabled() {
        return Transformations.map(gpsDisabledMutableLiveData, gpsDisabled -> gpsDisabled == null);
    }

    @Override
    public void requestGps() {
        requestGpsPing.setValue(true);
    }
}
