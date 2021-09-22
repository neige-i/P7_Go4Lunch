package com.neige_i.go4lunch.domain.gps;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class RequestGpsUseCaseImpl implements RequestGpsUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public RequestGpsUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void request() {
        locationRepository.requestGps();
    }
}
