package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;

import javax.inject.Inject;

public class GetLocationPermissionUseCaseImpl implements GetLocationPermissionUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetLocationPermissionUseCaseImpl(@NonNull LocationPermissionRepository locationPermissionRepository) {
        this.locationPermissionRepository = locationPermissionRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public boolean isGranted() {
        return locationPermissionRepository.isPermissionGranted();
    }
}
