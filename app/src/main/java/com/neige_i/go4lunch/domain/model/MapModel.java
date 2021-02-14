package com.neige_i.go4lunch.domain.model;

import android.location.Location;

import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.Objects;

public class MapModel {

    private final boolean locationPermission;
    @Nullable
    private final Location currentLocation;
    @Nullable
    private final NearbyResponse nearbyResponse;

    public MapModel(boolean locationPermission, @Nullable Location location, @Nullable NearbyResponse nearbyResponse) {
        this.locationPermission = locationPermission;
        currentLocation = location;
        this.nearbyResponse = nearbyResponse;
    }

    public boolean isLocationPermissionGranted() {
        return locationPermission;
    }

    @Nullable
    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Nullable
    public NearbyResponse getNearbyResponse() {
        return nearbyResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapModel mapModel = (MapModel) o;
        return locationPermission == mapModel.locationPermission &&
            Objects.equals(currentLocation, mapModel.currentLocation) &&
            Objects.equals(nearbyResponse, mapModel.nearbyResponse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationPermission, currentLocation, nearbyResponse);
    }
}
