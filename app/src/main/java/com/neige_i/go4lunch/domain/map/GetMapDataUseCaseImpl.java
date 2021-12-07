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
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
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

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<MapData> mapData = new MediatorLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MediatorLiveData<Map<String, Integer>> interestedWorkmatesMediatorLiveData = new MediatorLiveData<>();

    @NonNull
    private final List<String> queriedRestaurants = new ArrayList<>();

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    GetMapDataUseCaseImpl(
        @NonNull LocationPermissionRepository locationPermissionRepository,
        @NonNull LocationRepository locationRepository,
        @NonNull NearbyRepository nearbyRepository,
        @NonNull GpsStateChangeReceiver gpsStateChangeReceiver,
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull AutocompleteRepository autocompleteRepository
    ) {
        this.locationPermissionRepository = locationPermissionRepository;
        this.firestoreRepository = firestoreRepository;

        interestedWorkmatesMediatorLiveData.setValue(new HashMap<>());

        final LiveData<Location> currentLocationLiveData = locationRepository.getCurrentLocation();
        final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData = Transformations.switchMap(
            currentLocationLiveData, location -> nearbyRepository.getData(location)
        );
        final LiveData<Boolean> gpsStateLiveData = gpsStateChangeReceiver.getGpsState();
        final LiveData<String> searchQueryLiveData = autocompleteRepository.getCurrentSearchQuery();

        mapData.addSource(locationPermissionMutableLiveData, locationPermission -> combine(locationPermission, currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(currentLocationLiveData, location -> combine(locationPermissionMutableLiveData.getValue(), location, nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurants, gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(gpsStateLiveData, gpsState -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsState, interestedWorkmatesMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        mapData.addSource(interestedWorkmatesMediatorLiveData, interestedWorkmatesMap -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMap, searchQueryLiveData.getValue()));
        mapData.addSource(searchQueryLiveData, searchQuery -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), searchQuery));
    }

    private void combine(
        @Nullable Boolean isLocationPermissionGranted,
        @Nullable Location currentLocation,
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @Nullable Boolean isGpsEnabled,
        @Nullable Map<String, Integer> interestedWorkmatesMap,
        @Nullable String searchQuery
    ) {
        if (isLocationPermissionGranted == null || isGpsEnabled == null) {
            return;
        }

        if (interestedWorkmatesMap == null) {
            throw new IllegalStateException("Impossible state: map is initialized in the constructor!");
        }

        final List<MapRestaurant> mapRestaurants = new ArrayList<>();

        if (nearbyRestaurants != null) {
            for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
                final String placeId = nearbyRestaurant.getPlaceId();
                final String restaurantName = nearbyRestaurant.getName();

                if (!queriedRestaurants.contains(placeId)) {
                    queriedRestaurants.add(placeId);

                    // Query interested workmates count
                    interestedWorkmatesMediatorLiveData.addSource(
                        firestoreRepository.getWorkmatesEatingAt(placeId), users -> {

                            interestedWorkmatesMap.put(placeId, users.size());
                            interestedWorkmatesMediatorLiveData.setValue(interestedWorkmatesMap);
                        }
                    );
                }

                mapRestaurants.add(new MapRestaurant(
                    placeId,
                    restaurantName,
                    nearbyRestaurant.getLatitude(),
                    nearbyRestaurant.getLongitude(),
                    nearbyRestaurant.getAddress(),
                    searchQuery != null && restaurantName.toLowerCase().contains(searchQuery.toLowerCase())
                ));
            }
        }

        mapData.setValue(new MapData(
            isLocationPermissionGranted,
            currentLocation,
            mapRestaurants,
            isGpsEnabled,
            interestedWorkmatesMap
        ));
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
