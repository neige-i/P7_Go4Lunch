package com.neige_i.go4lunch.data.google_places;

import androidx.annotation.Nullable;

public interface CleanRestaurantDelegate {

    int getRating(@Nullable Double rating);

    @Nullable
    String getPhotoUrl(@Nullable String photoReference);
}
