package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.data.location.LocationRepository;

public class GetGpsResolvableUseCaseImpl implements GetGpsResolvableUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public GetGpsResolvableUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<ResolvableApiException> getResolvable() {
        return locationRepository.getEnableGpsEvent();
    }
}
