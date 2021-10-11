package com.neige_i.go4lunch.domain.place_nearby;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.location.LocationRepository;

import java.util.List;

import javax.inject.Inject;

public class GetNearbyRestaurantsUseCaseImpl implements GetNearbyRestaurantsUseCase {

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetNearbyRestaurantsUseCaseImpl(
        @NonNull LocationRepository locationRepository,
        @NonNull NearbyRepository nearbyRepository
    ) {
        nearbyRestaurantsLiveData =
            Transformations.switchMap(locationRepository.getCurrentLocation(), location ->
                nearbyRepository.getNearbyRestaurants(location)
            );
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<List<NearbyRestaurant>> get() {
        return nearbyRestaurantsLiveData;
    }
}
