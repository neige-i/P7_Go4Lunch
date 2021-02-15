package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

public interface UpdateSelectedRestaurantUseCase {

    void setSelectedRestaurant(@NonNull String placeId);

    void clearSelectedRestaurant();
}
