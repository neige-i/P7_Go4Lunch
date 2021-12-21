package com.neige_i.go4lunch.domain.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class MapData {

    private final boolean locationPermissionGranted;
    @Nullable
    private final Location currentLocation;
    @NonNull
    private final List<MapRestaurant> mapRestaurants;
    private final boolean gpsEnabled;
    @Nullable
    private final Location searchedRestaurantLocation;

    public MapData(
        boolean locationPermissionGranted,
        @Nullable Location currentLocation,
        @NonNull List<MapRestaurant> mapRestaurants,
        boolean gpsEnabled,
        @Nullable Location searchedRestaurantLocation
    ) {
        this.locationPermissionGranted = locationPermissionGranted;
        this.currentLocation = currentLocation;
        this.mapRestaurants = mapRestaurants;
        this.gpsEnabled = gpsEnabled;
        this.searchedRestaurantLocation = searchedRestaurantLocation;
    }

    public boolean isLocationPermissionGranted() {
        return locationPermissionGranted;
    }

    @Nullable
    public Location getCurrentLocation() {
        return currentLocation;
    }

    @NonNull
    public List<MapRestaurant> getMapRestaurants() {
        return mapRestaurants;
    }

    public boolean isGpsEnabled() {
        return gpsEnabled;
    }

    @Nullable
    public Location getSearchedRestaurantLocation() {
        return searchedRestaurantLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapData mapData = (MapData) o;
        return locationPermissionGranted == mapData.locationPermissionGranted &&
            gpsEnabled == mapData.gpsEnabled &&
            Objects.equals(currentLocation, mapData.currentLocation) &&
            Objects.equals(searchedRestaurantLocation, mapData.searchedRestaurantLocation) &&
            mapRestaurants.equals(mapData.mapRestaurants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationPermissionGranted, currentLocation, mapRestaurants, gpsEnabled, searchedRestaurantLocation);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapData{" +
            "locationPermissionGranted=" + locationPermissionGranted +
            ", currentLocation=" + currentLocation +
            ", mapRestaurants=" + mapRestaurants +
            ", gpsEnabled=" + gpsEnabled +
            ", searchedRestaurantLocation=" + searchedRestaurantLocation +
            '}';
    }
}