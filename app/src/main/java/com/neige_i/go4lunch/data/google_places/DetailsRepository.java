package com.neige_i.go4lunch.data.google_places;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;

public interface DetailsRepository {

    /**
     * Returns the details of the restaurant with the specified ID.
     */
    @NonNull
    LiveData<RestaurantDetails> getRestaurantDetails(@NonNull String placeId);
}
