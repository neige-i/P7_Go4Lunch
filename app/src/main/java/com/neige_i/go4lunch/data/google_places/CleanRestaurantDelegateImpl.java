package com.neige_i.go4lunch.data.google_places;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;

public class CleanRestaurantDelegateImpl implements CleanRestaurantDelegate {

    @NonNull
    private final String mapsApiKey;

    @Inject
    CleanRestaurantDelegateImpl(@NonNull String mapsApiKey) {
        this.mapsApiKey = mapsApiKey;
    }

    @Override
    public int getRating(@Nullable Double rating) {
        return rating == null ? -1 : (int) Math.round((rating - 1) * .75);
    }

    @Nullable
    @Override
    public String getPhotoUrl(@Nullable String photoReference) {
        if (photoReference != null) {
            return "https://maps.googleapis.com/" +
                "maps/api/place/photo?" +
                "maxheight=720" +
                "&key=" + mapsApiKey +
                "&photoreference=" + photoReference;
        } else {
            return null;
        }
    }
}
