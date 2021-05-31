package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;
import com.neige_i.go4lunch.data.location.LocationRepository;

public class GetLocationPermissionUseCaseImpl implements GetLocationPermissionUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;
    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public GetLocationPermissionUseCaseImpl(@NonNull LocationPermissionRepository locationPermissionRepository, @NonNull LocationRepository locationRepository) {
        this.locationPermissionRepository = locationPermissionRepository;
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<Boolean> isPermissionGranted() {
        return Transformations.map(locationPermissionRepository.getLocationPermission(), isPermissionGranted -> {
            if (isPermissionGranted)
                locationRepository.startLocationUpdates();
            else
                locationRepository.removeLocationUpdates();

            return isPermissionGranted;
        });
    }
}
