package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.Restaurant;

import java.util.List;

public class UpdateInterestedWorkmatesUseCaseImpl implements UpdateInterestedWorkmatesUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;

    public UpdateInterestedWorkmatesUseCaseImpl(@NonNull FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public void addWorkmateToList(@NonNull String restaurantId, @NonNull Restaurant.InterestedWorkmate interestedWorkmate) {

    }

    @Override
    public void removeWorkmateToList(@NonNull String restaurantId, @NonNull Restaurant.InterestedWorkmate interestedWorkmate) {

    }
}
