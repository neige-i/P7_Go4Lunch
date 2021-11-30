package com.neige_i.go4lunch.domain.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.google_places.AutocompleteRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.data.location.LocationPermissionRepository;
import com.neige_i.go4lunch.data.location.LocationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class GetMapDataUseCaseImpl implements GetMapDataUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;
    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final AutocompleteRepository autocompleteRepository;
    @NonNull
    private final DetailsRepository detailsRepository;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<MapData> mapData = new MediatorLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MediatorLiveData<Map<String, RestaurantDetails>> restaurantDetailsMediatorLiveData = new MediatorLiveData<>();
    @NonNull
    private final MediatorLiveData<Map<String, Integer>> interestedWorkmatesMediatorLiveData = new MediatorLiveData<>();

    @NonNull
    private final List<String> queriedRestaurants = new ArrayList<>();
    private String currentSearchQuery;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    GetMapDataUseCaseImpl(
        @NonNull LocationPermissionRepository locationPermissionRepository,
        @NonNull LocationRepository locationRepository,
        @NonNull NearbyRepository nearbyRepository,
        @NonNull GpsStateChangeReceiver gpsStateChangeReceiver,
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull AutocompleteRepository autocompleteRepository,
        @NonNull DetailsRepository detailsRepository
    ) {
        this.locationPermissionRepository = locationPermissionRepository;
        this.firestoreRepository = firestoreRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.detailsRepository = detailsRepository;

        interestedWorkmatesMediatorLiveData.setValue(new HashMap<>());
        restaurantDetailsMediatorLiveData.setValue(new HashMap<>());

        final LiveData<Location> currentLocationLiveData = locationRepository.getCurrentLocation();
        final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData = Transformations.switchMap(
            currentLocationLiveData, location -> nearbyRepository.getData(location)
        );
        final LiveData<Boolean> gpsStateLiveData = gpsStateChangeReceiver.getGpsState();
        final LiveData<String> searchQueryLiveData = autocompleteRepository.getCurrentSearchQuery();

        mapData.addSource(locationPermissionMutableLiveData, locationPermission -> combine(locationPermission, currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(currentLocationLiveData, location -> combine(locationPermissionMutableLiveData.getValue(), location, nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurants, gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(gpsStateLiveData, gpsState -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsState, interestedWorkmatesMediatorLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(interestedWorkmatesMediatorLiveData, interestedWorkmatesMap -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMap, restaurantDetailsMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(restaurantDetailsMediatorLiveData, restaurantDetailsMap -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), restaurantDetailsMap, searchQueryLiveData.getValue()));
        mapData.addSource(searchQueryLiveData, searchQuery -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), searchQuery));
    }

    private void combine(
        @Nullable Boolean isLocationPermissionGranted,
        @Nullable Location currentLocation,
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @Nullable Boolean isGpsEnabled,
        @Nullable Map<String, Integer> interestedWorkmatesMap,
        @Nullable Map<String, RestaurantDetails> restaurantDetailsMap,
        @Nullable String searchQuery
    ) {
        if (isLocationPermissionGranted == null || isGpsEnabled == null) {
            return;
        }

        if (restaurantDetailsMap == null || interestedWorkmatesMap == null) {
            throw new IllegalStateException("Impossible state: map is initialized in the constructor!");
        }

        final List<MapRestaurant> mapRestaurants = new ArrayList<>();
        boolean clearMarkers = false;

        if (searchQuery != null) {
            // Request autocomplete search if it is a new one
            if (!searchQuery.equals(currentSearchQuery)) {
                currentSearchQuery = searchQuery;
                clearMarkers = true;

                mapData.addSource(
                    autocompleteRepository.getData(searchQuery, currentLocation), autocompleteRestaurants -> {
                        // Clear collections because the autocomplete request needs fresh data
                        queriedRestaurants.clear();
                        restaurantDetailsMap.clear();
                        interestedWorkmatesMap.clear();

                        for (AutocompleteRestaurant autocompleteRestaurant : autocompleteRestaurants) {
                            queryDetails(autocompleteRestaurant.getPlaceId());
                            queryWorkmates(autocompleteRestaurant.getPlaceId());
                        }
                    }
                );
            }

            for (RestaurantDetails restaurantDetails : restaurantDetailsMap.values()) {
                mapRestaurants.add(new MapRestaurant(
                    restaurantDetails.getPlaceId(),
                    restaurantDetails.getName(),
                    restaurantDetails.getLatitude(),
                    restaurantDetails.getLongitude(),
                    restaurantDetails.getAddress()
                ));
            }
        } else {
            if (currentSearchQuery != null) {
                currentSearchQuery = null;
                clearMarkers = true;

                // Clear collections because need a fresh request
                queriedRestaurants.clear();
                restaurantDetailsMap.clear();
                interestedWorkmatesMap.clear();
            }

            if (nearbyRestaurants != null) {
                for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
                    final String placeId = nearbyRestaurant.getPlaceId();

                    queryWorkmates(nearbyRestaurant.getPlaceId());

                    mapRestaurants.add(new MapRestaurant(
                        placeId,
                        nearbyRestaurant.getName(),
                        nearbyRestaurant.getLatitude(),
                        nearbyRestaurant.getLongitude(),
                        nearbyRestaurant.getAddress()
                    ));
                }
            }
        }

        mapData.setValue(new MapData(
            isLocationPermissionGranted,
            currentLocation,
            mapRestaurants,
            isGpsEnabled,
            interestedWorkmatesMap,
            clearMarkers
        ));
    }

    private void queryDetails(@NonNull String restaurantId) {
        final Map<String, RestaurantDetails> restaurantDetailsMap = restaurantDetailsMediatorLiveData.getValue();

        if (restaurantDetailsMap == null) {
            throw new IllegalStateException("Impossible state: maps are initialized in the constructor!");
        }

        if (!queriedRestaurants.contains(restaurantId)) {
            queriedRestaurants.add(restaurantId);

            // Query restaurants details
            restaurantDetailsMediatorLiveData.addSource(
                detailsRepository.getData(restaurantId), restaurantDetails -> {

                    restaurantDetailsMap.put(restaurantId, restaurantDetails);
                    restaurantDetailsMediatorLiveData.setValue(restaurantDetailsMap);
                }
            );
        }
    }

    private void queryWorkmates(@NonNull String restaurantId) {
        final Map<String, Integer> interestedWorkmatesMap = interestedWorkmatesMediatorLiveData.getValue();

        if (interestedWorkmatesMap == null) {
            throw new IllegalStateException("Impossible state: maps are initialized in the constructor!");
        }

        if (!queriedRestaurants.contains(restaurantId)) {
            queriedRestaurants.add(restaurantId);

            // Query interested workmates count
            interestedWorkmatesMediatorLiveData.addSource(
                firestoreRepository.getWorkmatesEatingAt(restaurantId), users -> {

                    interestedWorkmatesMap.put(restaurantId, users.size());
                    interestedWorkmatesMediatorLiveData.setValue(interestedWorkmatesMap);
                }
            );
        }
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<MapData> get() {
        return mapData;
    }

    @Override
    public void refresh() {
        // The MutableLiveData below is used to force setting MapData.
        // Without it, MapData depends only on GPS, location and nearby restaurants and
        // when the GPS is already turned off, no value is emitted from any of these sources
        // The problem is, When the activity is resumed with granted location permission and the GPS is disabled,
        // the location button should be displayed  but isn't because MapData's value is not updated
        // This is why refresh() is called when the activity is resumed, to force MapData emit a value

        locationPermissionMutableLiveData.setValue(locationPermissionRepository.isPermissionGranted());
    }
}
