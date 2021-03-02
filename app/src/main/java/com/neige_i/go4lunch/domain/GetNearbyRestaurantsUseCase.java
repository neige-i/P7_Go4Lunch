package com.neige_i.go4lunch.domain;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.Objects;

public interface GetNearbyRestaurantsUseCase {

    @NonNull
    LiveData<MapWrapper> getNearby();

    class MapWrapper {

        private final boolean locationPermission;
        @Nullable
        private final Location currentLocation;
        @Nullable
        private final NearbyResponse nearbyResponse;

        public MapWrapper(boolean locationPermission, @Nullable Location location, @Nullable NearbyResponse nearbyResponse) {
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
            MapWrapper mapWrapper = (MapWrapper) o;
            return locationPermission == mapWrapper.locationPermission &&
                Objects.equals(currentLocation, mapWrapper.currentLocation) &&
                Objects.equals(nearbyResponse, mapWrapper.nearbyResponse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(locationPermission, currentLocation, nearbyResponse);
        }

        @NonNull
        @Override
        public String toString() {
            return "MapWrapper{" +
                "locationPermission=" + locationPermission +
                ", currentLocation=" + currentLocation +
                ", nearbyResponse=" + nearbyResponse +
                '}';
        }
    }
}
