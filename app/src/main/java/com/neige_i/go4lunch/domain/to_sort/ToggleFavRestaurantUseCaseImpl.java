package com.neige_i.go4lunch.domain.to_sort;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;

import javax.inject.Inject;

public class ToggleFavRestaurantUseCaseImpl implements ToggleFavRestaurantUseCase {

    @NonNull
    private final FirebaseRepository firebaseRepository;

    @Inject
    public ToggleFavRestaurantUseCaseImpl(@NonNull FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    public void toggleFavorite(@NonNull String placeId) {
        firebaseRepository.toggleFavoriteRestaurant(placeId);
    }
}
