package com.neige_i.go4lunch.domain.detail;

import androidx.annotation.NonNull;

public interface UpdateRestaurantPrefUseCase {

    void like(@NonNull String placeId);

    void unlike(@NonNull String placeId);

    void select(
        @NonNull String placeId,
        @NonNull String restaurantName,
        @NonNull String restaurantAddress
    );

    void unselect();
}
