package com.neige_i.go4lunch.domain.map;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationRepository;

import javax.inject.Inject;

public class RequestGpsUseCaseImpl implements RequestGpsUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    RequestGpsUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void request() {
        locationRepository.requestGpsDialog();
    }
}
