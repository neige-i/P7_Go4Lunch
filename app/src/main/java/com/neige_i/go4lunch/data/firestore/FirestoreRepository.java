package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface FirestoreRepository {

    @NonNull
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @NonNull
    LiveData<User> getUser(@NonNull String userId);

    /**
     * Gets the {@link User} synchronously (=blocking). Must be called from a background thread.
     */
    @Nullable
    User getUserByIdSync(@NonNull String userId);

    void addUser(@NonNull String userId, @NonNull User user);

    @NonNull
    LiveData<List<User>> getWorkmatesEatingAt(@NonNull String restaurantId);

    @NonNull
    List<User> getWorkmatesEatingAtSync(@NonNull String restaurantId);

    @NonNull
    LiveData<List<User>> getAllUsers();

    void addToFavoriteRestaurant(@NonNull String userId, @NonNull String placeId);

    void removeFromFavoriteRestaurant(@NonNull String userId, @NonNull String placeId);

    void setSelectedRestaurant(
        @NonNull String userId,
        @NonNull String placeId,
        @NonNull String restaurantName,
        @NonNull String restaurantAddress
    );

    void clearSelectedRestaurant(@NonNull String userId);

    void removeListenerRegistrations();
}
