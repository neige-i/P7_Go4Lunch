package com.neige_i.go4lunch.domain;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.List;
import java.util.Objects;

public interface GetRestaurantDetailsListUseCase {

    @NonNull
    LiveData<ListWrapper> getDetailsList();

    class ListWrapper {

        @NonNull
        private final NearbyResponse nearbyResponse;
        @NonNull
        private final List<DetailsResponse> detailsResponses;
        @Nullable
        private final Location currentLocation;
        @NonNull
        private final List<Restaurant> restaurants;

        public ListWrapper(@NonNull NearbyResponse nearbyResponse,
                           @NonNull List<DetailsResponse> detailsResponses,
                           @Nullable Location currentLocation,
                           @NonNull List<Restaurant> restaurants
        ) {
            this.nearbyResponse = nearbyResponse;
            this.detailsResponses = detailsResponses;
            this.currentLocation = currentLocation;
            this.restaurants = restaurants;
        }

        @NonNull
        public NearbyResponse getNearbyResponse() {
            return nearbyResponse;
        }

        @NonNull
        public List<DetailsResponse> getDetailsResponses() {
            return detailsResponses;
        }

        @Nullable
        public Location getCurrentLocation() {
            return currentLocation;
        }

        @NonNull
        public List<Restaurant> getRestaurants() {
            return restaurants;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListWrapper that = (ListWrapper) o;
            return nearbyResponse.equals(that.nearbyResponse) &&
                detailsResponses.equals(that.detailsResponses) &&
                Objects.equals(currentLocation, that.currentLocation) &&
                restaurants.equals(that.restaurants);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nearbyResponse, detailsResponses, currentLocation, restaurants);
        }

        @NonNull
        @Override
        public String toString() {
            return "ListWrapper{" +
                "nearbyResponse=" + nearbyResponse +
                ", detailsResponses=" + detailsResponses +
                ", currentLocation=" + currentLocation +
                ", restaurants=" + restaurants +
                '}';
        }
    }
}
