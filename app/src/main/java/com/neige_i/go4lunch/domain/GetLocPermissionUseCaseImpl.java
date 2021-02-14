package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class GetLocPermissionUseCaseImpl implements GetLocPermissionUseCase {

    @NonNull
    private final LocationRepository locationRepository;

    public GetLocPermissionUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @NonNull
    @Override
    public LiveData<Boolean> isPermissionGranted() {
        return Transformations.map(locationRepository.getLocationPermission(), isPermissionGranted -> {
            if (isPermissionGranted)
                locationRepository.startLocationUpdates();
            else
                locationRepository.removeLocationUpdates();

            return isPermissionGranted;
        });
    }
}
