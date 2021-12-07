package com.neige_i.go4lunch.domain.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapData {

    private final boolean locationPermissionGranted;
    @Nullable
    private final Location currentLocation;
    @NonNull
    private final List<MapRestaurant> mapRestaurants;
    private final boolean gpsEnabled;
    @NonNull
    private final Map<String, Integer> interestedWorkmates;

    MapData(
        boolean locationPermissionGranted,
        @Nullable Location currentLocation,
        @NonNull List<MapRestaurant> mapRestaurants,
        boolean gpsEnabled,
        @NonNull Map<String, Integer> interestedWorkmates
    ) {
        this.locationPermissionGranted = locationPermissionGranted;
        this.currentLocation = currentLocation;
        this.mapRestaurants = mapRestaurants;
        this.gpsEnabled = gpsEnabled;
        this.interestedWorkmates = interestedWorkmates;
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

    @NonNull
    public Map<String, Integer> getInterestedWorkmates() {
        return interestedWorkmates;
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
            mapRestaurants.equals(mapData.mapRestaurants) &&
            interestedWorkmates.equals(mapData.interestedWorkmates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationPermissionGranted, currentLocation, mapRestaurants, gpsEnabled, interestedWorkmates);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapData{" +
            "locationPermissionGranted=" + locationPermissionGranted +
            ", currentLocation=" + currentLocation +
            ", mapRestaurants=" + mapRestaurants +
            ", gpsEnabled=" + gpsEnabled +
            ", interestedWorkmates=" + interestedWorkmates +
            '}';
    }
}