package com.neige_i.go4lunch.domain.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class GetLocationUseCaseImpl implements GetLocationUseCase {

    @NonNull
    private final LocationRepository locationRepository;

    public GetLocationUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @NonNull
    @Override
    public LiveData<Location> get() {
        return locationRepository.getCurrentLocation();
    }
}
