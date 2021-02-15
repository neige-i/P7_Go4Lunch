package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;

public class UpdateSelectedRestaurantUseCaseImpl implements UpdateSelectedRestaurantUseCase {

    @NonNull
    private final FirebaseRepository firebaseRepository;

    public UpdateSelectedRestaurantUseCaseImpl(@NonNull FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    public void setSelectedRestaurant(@NonNull String placeId) {
        firebaseRepository.setSelectedRestaurant(placeId);
    }

    @Override
    public void clearSelectedRestaurant() {
        firebaseRepository.clearSelectedRestaurant();
    }
}
