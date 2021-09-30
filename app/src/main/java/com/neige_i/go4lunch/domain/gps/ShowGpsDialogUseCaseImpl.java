package com.neige_i.go4lunch.domain.gps;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.data.location.LocationRepository;

public class ShowGpsDialogUseCaseImpl implements ShowGpsDialogUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public ShowGpsDialogUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<ResolvableApiException> getDialog() {
        return locationRepository.getGpsDialog();
    }
}
