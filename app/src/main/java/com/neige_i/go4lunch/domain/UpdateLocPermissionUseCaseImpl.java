package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class UpdateLocPermissionUseCaseImpl implements UpdateLocPermissionUseCase {

    @NonNull private final LocationRepository locationRepository;

    public UpdateLocPermissionUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void updatePermission(boolean isPermissionGranted) {
        locationRepository.updateLocationPermission(isPermissionGranted);
    }
}
