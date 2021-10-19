package com.neige_i.go4lunch.domain.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.data.location.LocationPermissionRepository;
import com.neige_i.go4lunch.data.location.LocationRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetMapDataUseCaseImpl implements GetMapDataUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<MapData> mapData = new MediatorLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    GetMapDataUseCaseImpl(
        @NonNull LocationPermissionRepository locationPermissionRepository,
        @NonNull LocationRepository locationRepository,
        @NonNull NearbyRepository nearbyRepository,
        @NonNull GpsStateChangeReceiver gpsStateChangeReceiver
    ) {
        this.locationPermissionRepository = locationPermissionRepository;

        final LiveData<Location> currentLocationLiveData = locationRepository.getCurrentLocation();
        final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData = Transformations.switchMap(
            currentLocationLiveData, location -> nearbyRepository.getNearbyRestaurants(location)
        );
        final LiveData<Boolean> gpsStateLiveData = gpsStateChangeReceiver.getGpsState();

        // ASKME: nearby should be the first source to make the tests pass (no problem when running)
        mapData.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurants, gpsStateLiveData.getValue()));
        mapData.addSource(locationPermissionMutableLiveData, locationPermission -> combine(locationPermission, currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue()));
        mapData.addSource(currentLocationLiveData, location -> combine(locationPermissionMutableLiveData.getValue(), location, nearbyRestaurantsLiveData.getValue(), gpsStateLiveData.getValue()));
        mapData.addSource(gpsStateLiveData, gpsState -> combine(locationPermissionMutableLiveData.getValue(), currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), gpsState));
    }

    private void combine(
        @Nullable Boolean isLocationPermissionGranted,
        @Nullable Location currentLocation,
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @Nullable Boolean isGpsEnabled
    ) {
        if (isLocationPermissionGranted == null || isGpsEnabled == null) {
            return;
        }

        mapData.setValue(new MapData(
            isLocationPermissionGranted,
            currentLocation,
            nearbyRestaurants != null ? nearbyRestaurants : Collections.emptyList(),
            isGpsEnabled
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
