package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;

public class GetLocationPermissionUseCaseImpl implements GetLocationPermissionUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public GetLocationPermissionUseCaseImpl(@NonNull LocationPermissionRepository locationPermissionRepository) {
        this.locationPermissionRepository = locationPermissionRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<Boolean> isGranted() {
        return locationPermissionRepository.getLocationPermission();
    }
}
