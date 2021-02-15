package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

public interface ToggleFavRestaurantUseCase {

    void toggleFavorite(@NonNull String placeId);
}
