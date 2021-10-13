package com.neige_i.go4lunch.domain.google_places;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;

import java.util.Map;

public interface GetNearbyRestaurantDetailsUseCase {

    @NonNull
    LiveData<Map<String, RestaurantDetails>> get();
}
