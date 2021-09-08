package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;

public class SetLocationPermissionUseCaseImpl implements SetLocationPermissionUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public SetLocationPermissionUseCaseImpl(@NonNull LocationPermissionRepository locationPermissionRepository) {
        this.locationPermissionRepository = locationPermissionRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void set(boolean locationPermission) {
        locationPermissionRepository.setLocationPermission(locationPermission);
    }
}
