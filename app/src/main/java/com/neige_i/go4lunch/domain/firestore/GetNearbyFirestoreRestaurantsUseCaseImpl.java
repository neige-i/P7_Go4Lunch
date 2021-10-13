package com.neige_i.go4lunch.domain.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantsUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetNearbyFirestoreRestaurantsUseCaseImpl implements GetNearbyFirestoreRestaurantsUseCase {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MediatorLiveData<List<Restaurant>> nearbyFirestoreRestaurantsMediatorLiveData = new MediatorLiveData<>();

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetNearbyFirestoreRestaurantsUseCaseImpl(
        @NonNull GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase,
        @NonNull FirestoreRepository firestoreRepository
    ) {
        nearbyFirestoreRestaurantsMediatorLiveData.addSource(getNearbyRestaurantsUseCase.get(), nearbyRestaurants -> {
            final List<Restaurant> nearbyFirestoreRestaurants = new ArrayList<>();

            for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
                nearbyFirestoreRestaurantsMediatorLiveData.addSource(
                    firestoreRepository.getRestaurantById(nearbyRestaurant.getPlaceId()), restaurant -> {

                        if (restaurant != null) {
                            nearbyFirestoreRestaurants.add(restaurant);
                        }
                    });
            }

            // Set value outside the "for" loop, no need to trigger listeners for each restaurant
            nearbyFirestoreRestaurantsMediatorLiveData.setValue(nearbyFirestoreRestaurants);
        });
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public LiveData<List<Restaurant>> get() {
        return nearbyFirestoreRestaurantsMediatorLiveData;
    }
}
