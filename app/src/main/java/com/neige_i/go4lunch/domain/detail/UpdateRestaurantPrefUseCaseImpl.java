package com.neige_i.go4lunch.domain.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;

import javax.inject.Inject;

public class UpdateRestaurantPrefUseCaseImpl implements UpdateRestaurantPrefUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @Nullable
    private final String currentUserId;

    @Inject
    public UpdateRestaurantPrefUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreRepository = firestoreRepository;

        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        } else {
            currentUserId = null;
        }
    }

    @Override
    public void like(@NonNull String placeId) {
        if (currentUserId != null) {
            firestoreRepository.addToFavoriteRestaurant(currentUserId, placeId);
        }
    }

    @Override
    public void unlike(@NonNull String placeId) {
        if (currentUserId != null) {
            firestoreRepository.removeFromFavoriteRestaurant(currentUserId, placeId);
        }
    }

    @Override
    public void select(@NonNull String placeId, @NonNull String restaurantName) {
        if (currentUserId != null) {
            firestoreRepository.setSelectedRestaurant(currentUserId, placeId, restaurantName);
        }
    }

    @Override
    public void unselect() {
        if (currentUserId != null) {
            firestoreRepository.clearSelectedRestaurant(currentUserId);
        }
    }
}
