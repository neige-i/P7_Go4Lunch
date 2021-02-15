package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;

public class ToggleFavRestaurantUseCaseImpl implements ToggleFavRestaurantUseCase {

    @NonNull
    private final FirebaseRepository firebaseRepository;

    public ToggleFavRestaurantUseCaseImpl(@NonNull FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    public void toggleFavorite(@NonNull String placeId) {
        firebaseRepository.toggleFavoriteRestaurant(placeId);
    }
}
