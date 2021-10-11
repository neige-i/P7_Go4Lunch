package com.neige_i.go4lunch.domain.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.location.LocationRepository;

import javax.inject.Inject;

public class GetLocationUseCaseImpl implements GetLocationUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetLocationUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<Location> get() {
        return locationRepository.getCurrentLocation();
    }
}
