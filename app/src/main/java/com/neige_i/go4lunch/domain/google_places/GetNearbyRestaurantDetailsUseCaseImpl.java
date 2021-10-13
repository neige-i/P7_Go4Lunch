package com.neige_i.go4lunch.domain.google_places;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class GetNearbyRestaurantDetailsUseCaseImpl implements GetNearbyRestaurantDetailsUseCase {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MediatorLiveData<Map<String, RestaurantDetails>> restaurantDetailsMapMediatorLiveData = new MediatorLiveData<>();

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetNearbyRestaurantDetailsUseCaseImpl(
        @NonNull GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase,
        @NonNull DetailsRepository detailsRepository
    ) {
        // ASKME: is correct, UseCase-ception
        restaurantDetailsMapMediatorLiveData.addSource(getNearbyRestaurantsUseCase.get(), nearbyRestaurants -> {
            final Map<String, RestaurantDetails> restaurantDetailsMap = new HashMap<>();

            for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
                final String restaurantId = nearbyRestaurant.getPlaceId();

                restaurantDetailsMapMediatorLiveData.addSource(
                    detailsRepository.getRestaurantDetails(restaurantId), restaurantDetails -> {

                        restaurantDetailsMap.put(restaurantId, restaurantDetails);

                        restaurantDetailsMapMediatorLiveData.setValue(restaurantDetailsMap);
                    }
                );
            }
        });
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<Map<String, RestaurantDetails>> get() {
        return restaurantDetailsMapMediatorLiveData;
    }
}
