package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.repository.location.LocationRepository;

import javax.inject.Inject;

public class SetLocationUpdatesUseCaseImpl implements SetLocationUpdatesUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    SetLocationUpdatesUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void set(boolean enableUpdates) {
        if (enableUpdates) {
            // Request GPS the first time location updates need to be started
            if (locationRepository.areLocationUpdatesNeverStarted()) {
                locationRepository.requestGpsDialog();
            }

            locationRepository.startLocationUpdates();
        } else {
            locationRepository.removeLocationUpdates();
        }
    }
}
