package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;

import java.util.Objects;

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
    public void setPermission(boolean isPermissionGranted) {
        final Boolean currentPermission = locationPermissionRepository.getLocationPermission().getValue();

        // Update permission only if the new value is different from the current one
        if (!Objects.equals(currentPermission, isPermissionGranted)) {
            locationPermissionRepository.updateLocationPermission(isPermissionGranted);
        }
    }
}
