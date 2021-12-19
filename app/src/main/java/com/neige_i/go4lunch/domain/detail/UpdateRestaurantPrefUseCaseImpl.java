package com.neige_i.go4lunch.domain.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.repository.firestore.FirestoreRepository;

import javax.inject.Inject;

public class UpdateRestaurantPrefUseCaseImpl implements UpdateRestaurantPrefUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    public UpdateRestaurantPrefUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreRepository = firestoreRepository;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void like(@NonNull String placeId) {
        if (getCurrentUserId() != null) {
            firestoreRepository.addToFavoriteRestaurant(getCurrentUserId(), placeId);
        }
    }

    @Override
    public void unlike(@NonNull String placeId) {
        if (getCurrentUserId() != null) {
            firestoreRepository.removeFromFavoriteRestaurant(getCurrentUserId(), placeId);
        }
    }

    @Override
    public void select(
        @NonNull String placeId,
        @NonNull String restaurantName,
        @NonNull String restaurantAddress
    ) {
        if (getCurrentUserId() != null) {
            firestoreRepository.setSelectedRestaurant(
                getCurrentUserId(),
                placeId,
                restaurantName,
                restaurantAddress
            );
        }
    }

    @Override
    public void unselect() {
        if (getCurrentUserId() != null) {
            firestoreRepository.clearSelectedRestaurant(getCurrentUserId());
        }
    }

    @Nullable
    private String getCurrentUserId() {
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }
}
