package com.neige_i.go4lunch.domain.gps;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;

import javax.inject.Inject;

public class GetGpsStatusUseCaseImpl implements GetGpsStatusUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GpsStateChangeReceiver gpsStateChangeReceiver;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetGpsStatusUseCaseImpl(@NonNull GpsStateChangeReceiver gpsStateChangeReceiver) {
        this.gpsStateChangeReceiver = gpsStateChangeReceiver;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<Boolean> isEnabled() {
        return gpsStateChangeReceiver.getGpsState();
    }
}
