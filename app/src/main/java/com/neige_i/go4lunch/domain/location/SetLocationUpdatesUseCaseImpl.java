package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class SetLocationUpdatesUseCaseImpl implements SetLocationUpdatesUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public SetLocationUpdatesUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void set(boolean enableUpdates) {
        if (enableUpdates) {
            locationRepository.startLocationUpdates();
        } else {
            locationRepository.removeLocationUpdates();
        }
    }
}
