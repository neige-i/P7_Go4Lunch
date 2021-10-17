package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.data.location.LocationRepository;

import javax.inject.Inject;

public class ShowGpsDialogUseCaseImpl implements ShowGpsDialogUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    ShowGpsDialogUseCaseImpl(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<ResolvableApiException> getDialog() {
        return locationRepository.getGpsDialog();
    }
}
