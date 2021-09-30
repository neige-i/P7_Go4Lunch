package com.neige_i.go4lunch.data.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class GpsStateChangeReceiver extends BroadcastReceiver {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationManager locationManager;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<Boolean> gpsStateMutableLiveData = new MutableLiveData<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public GpsStateChangeReceiver(@NonNull LocationManager locationManager) {
        this.locationManager = locationManager;

        // Init GPS state (the receiver is only triggered when the state is CHANGED)
        setGpsState();
    }

    @NonNull
    public LiveData<Boolean> getGpsState() {
        // The receiver is called twice, one for each provider: GPS & network
        return Transformations.distinctUntilChanged(gpsStateMutableLiveData);
    }

    // ------------------------------------- RECEIVER METHODS --------------------------------------

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            // Update GPS state
            setGpsState();
        }
    }

    private void setGpsState() {
        gpsStateMutableLiveData.setValue(
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        );
    }
}
