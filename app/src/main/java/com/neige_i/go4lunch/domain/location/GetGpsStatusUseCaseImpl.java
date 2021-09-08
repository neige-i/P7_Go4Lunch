package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.location.LocationRepository;

public class GetGpsStatusUseCaseImpl implements GetGpsStatusUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public GetGpsStatusUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<Boolean> isEnabled() {
        return locationRepository.isGpsEnabled();
    }
}
