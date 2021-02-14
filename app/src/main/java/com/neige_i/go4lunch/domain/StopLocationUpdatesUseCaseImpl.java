package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class StopLocationUpdatesUseCaseImpl implements StopLocationUpdatesUseCase {

    @NonNull
    private final LocationRepository locationRepository;

    public StopLocationUpdatesUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void stopUpdates() {
        locationRepository.removeLocationUpdates();
    }
}
