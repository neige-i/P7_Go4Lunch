package com.neige_i.go4lunch.domain.place_nearby;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;

import java.util.List;

public interface GetNearbyRestaurantsUseCase {

    @NonNull
    LiveData<List<NearbyRestaurant>> get();
}
