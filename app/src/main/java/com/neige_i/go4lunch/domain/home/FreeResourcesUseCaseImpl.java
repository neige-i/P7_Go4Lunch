package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.repository.firestore.FirestoreRepository;
import com.neige_i.go4lunch.repository.location.LocationRepository;

import javax.inject.Inject;

public class FreeResourcesUseCaseImpl implements FreeResourcesUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final FirestoreRepository firestoreRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    FreeResourcesUseCaseImpl(
        @NonNull LocationRepository locationRepository,
        @NonNull FirestoreRepository firestoreRepository
    ) {
        this.locationRepository = locationRepository;
        this.firestoreRepository = firestoreRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void execute() {
        locationRepository.removeLocationUpdates();
        firestoreRepository.removeListenerRegistrations();
    }
}
