package com.neige_i.go4lunch.domain.to_sort;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.model.User;

public interface UpdateSelectedRestaurantUseCase {

    void selectRestaurant(@NonNull String userId, @NonNull User.SelectedRestaurant selectedRestaurant);

    void clearRestaurant(@NonNull String userId);
}
