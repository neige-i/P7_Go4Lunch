package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class StopLocationUpdatesUseCaseImpl implements StopLocationUpdatesUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public StopLocationUpdatesUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void stopUpdates() {
        locationRepository.removeLocationUpdates();
    }
}
