package com.neige_i.go4lunch.domain.to_sort;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.User;

import javax.inject.Inject;

public class UpdateSelectedRestaurantUseCaseImpl implements UpdateSelectedRestaurantUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;

    @Inject
    public UpdateSelectedRestaurantUseCaseImpl(@NonNull FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public void selectRestaurant(
        @NonNull String userId,
        @NonNull User.SelectedRestaurant selectedRestaurant
    ) {
        firestoreRepository.setSelectedRestaurant(userId, selectedRestaurant);
    }

    @Override
    public void clearRestaurant(@NonNull String userId) {
        firestoreRepository.clearSelectedRestaurant(userId);
    }
}
