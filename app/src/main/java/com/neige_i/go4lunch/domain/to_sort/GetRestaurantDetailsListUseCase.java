package com.neige_i.go4lunch.domain.to_sort;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.google_places.model.DetailsRestaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;

import java.util.List;
import java.util.Objects;

public interface GetRestaurantDetailsListUseCase {

    @NonNull
    LiveData<ListWrapper> getDetailsList();

    class ListWrapper {

        @NonNull
        private final List<NearbyRestaurant> rawNearbyResponse;
        @NonNull
        private final List<DetailsRestaurant> detailsRestaurants;
        @Nullable
        private final Location currentLocation;
        @NonNull
        private final List<Restaurant> restaurants;

        public ListWrapper(@NonNull List<NearbyRestaurant> rawNearbyResponse,
                           @NonNull List<DetailsRestaurant> detailsRestaurants,
                           @Nullable Location currentLocation,
                           @NonNull List<Restaurant> restaurants
        ) {
            this.rawNearbyResponse = rawNearbyResponse;
            this.detailsRestaurants = detailsRestaurants;
            this.currentLocation = currentLocation;
            this.restaurants = restaurants;
        }

        @NonNull
        public List<NearbyRestaurant> getNearbyRestaurants() {
            return rawNearbyResponse;
        }

        @NonNull
        public List<DetailsRestaurant> getDetailsResponses() {
            return detailsRestaurants;
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
            return rawNearbyResponse.equals(that.rawNearbyResponse) &&
                detailsRestaurants.equals(that.detailsRestaurants) &&
                Objects.equals(currentLocation, that.currentLocation) &&
                restaurants.equals(that.restaurants);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rawNearbyResponse, detailsRestaurants, currentLocation, restaurants);
        }

        @NonNull
        @Override
        public String toString() {
            return "ListWrapper{" +
                "nearbyResponse=" + rawNearbyResponse +
                ", detailsResponses=" + detailsRestaurants +
                ", currentLocation=" + currentLocation +
                ", restaurants=" + restaurants +
                '}';
        }
    }
}
