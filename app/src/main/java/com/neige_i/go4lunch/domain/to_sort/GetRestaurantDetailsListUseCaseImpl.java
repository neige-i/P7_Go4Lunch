package com.neige_i.go4lunch.domain.to_sort;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsRestaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.location.LocationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class GetRestaurantDetailsListUseCaseImpl implements GetRestaurantDetailsListUseCase {

    @NonNull
    private final DetailsRepository detailsRepository;

    @NonNull
    private final MediatorLiveData<ListWrapper> listMediatorLiveData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<Map<String, DetailsRestaurant>> placeIdDetailsResponseMapMediatorLiveData = new MediatorLiveData<>();
    @NonNull
    private final List<String> queriedPlaceIds = new ArrayList<>();
    @Nullable
    private Location deviceLocation;

    @Inject
    public GetRestaurantDetailsListUseCaseImpl(
        @NonNull LocationRepository locationRepository,
        @NonNull NearbyRepository nearbyRepository,
        @NonNull DetailsRepository detailsRepository,
        @NonNull FirestoreRepository firestoreRepository
    ) {
        this.detailsRepository = detailsRepository;

        placeIdDetailsResponseMapMediatorLiveData.setValue(new HashMap<>());

        // 1. FETCH the current location
        final LiveData<List<NearbyRestaurant>> nearbyRestaurantLiveData =
            Transformations.switchMap(locationRepository.getCurrentLocation(), currentLocation -> {
                deviceLocation = currentLocation;
                return nearbyRepository.getNearbyRestaurants(currentLocation);
            });

        final LiveData<List<Restaurant>> restaurantListLiveData = firestoreRepository.getAllRestaurants();

        listMediatorLiveData.addSource(nearbyRestaurantLiveData, nearbyResponse ->
            combine(nearbyResponse, placeIdDetailsResponseMapMediatorLiveData.getValue(), restaurantListLiveData.getValue())
        );
        listMediatorLiveData.addSource(placeIdDetailsResponseMapMediatorLiveData, placeIdDetailsResponseMap ->
            combine(nearbyRestaurantLiveData.getValue(), placeIdDetailsResponseMap, restaurantListLiveData.getValue())
        );
        listMediatorLiveData.addSource(restaurantListLiveData, restaurants ->
            combine(nearbyRestaurantLiveData.getValue(), placeIdDetailsResponseMapMediatorLiveData.getValue(), restaurants)
        );
    }

    private void combine(
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @Nullable Map<String, DetailsRestaurant> placeIdDetailsResponseMap,
        @Nullable List<Restaurant> restaurantList
    ) {
        if (nearbyRestaurants == null) {
            return;
        }

        final List<Restaurant> restaurants = restaurantList != null ? restaurantList : Collections.emptyList();

        if (placeIdDetailsResponseMap == null) {
            throw new IllegalStateException("Impossible state : map is always initialized !");
        }

        for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
            final String placeId = nearbyRestaurant.getPlaceId();

            if (!queriedPlaceIds.contains(placeId)) {

                queriedPlaceIds.add(placeId);

                // 3. FETCH the restaurant details with the place ID for each one of them
                placeIdDetailsResponseMapMediatorLiveData.addSource(
                    detailsRepository.getDetailsRestaurant(placeId), newDetailsResponse -> {

                        // 4. ADD the new details response to the list and UPDATE LiveData

                        placeIdDetailsResponseMap.put(placeId, newDetailsResponse);

                        placeIdDetailsResponseMapMediatorLiveData.setValue(placeIdDetailsResponseMap);
                    }
                );
            }
        }

        listMediatorLiveData.setValue(new ListWrapper(
            nearbyRestaurants,
            new ArrayList<>(placeIdDetailsResponseMap.values()),
            deviceLocation,
            restaurants
        ));
    }

    @NonNull
    @Override
    public LiveData<ListWrapper> getDetailsList() {
        return listMediatorLiveData;
    }
}
