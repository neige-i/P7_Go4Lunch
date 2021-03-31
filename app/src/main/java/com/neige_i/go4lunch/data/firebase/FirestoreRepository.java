package com.neige_i.go4lunch.data.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

public interface FirestoreRepository {

    @NonNull
    LiveData<User> getUser(@NonNull String userId);

    void addUser(@NonNull String userId, @NonNull User userToAdd);

    @NonNull
    LiveData<List<User>> getAllUsers();

    void setSelectedRestaurant(@NonNull String userId, @NonNull User.SelectedRestaurant selectedRestaurant);

    void clearSelectedRestaurant(@NonNull String userId);

    @NonNull
    LiveData<List<Restaurant>> getAllRestaurants();

    @NonNull
    LiveData<Restaurant> getRestaurant(@NonNull String restaurantId);

    void addInterestedWorkmate(@NonNull String restaurantId, @NonNull String workmateId);
}
