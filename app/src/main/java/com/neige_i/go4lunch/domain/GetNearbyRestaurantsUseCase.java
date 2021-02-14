package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.domain.model.MapModel;

public interface GetNearbyRestaurantsUseCase {

    @NonNull
    LiveData<MapModel> getNearby();
}
