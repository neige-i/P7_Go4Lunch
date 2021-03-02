package com.neige_i.go4lunch.domain;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

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

        public ListWrapper(@NonNull NearbyResponse nearbyResponse,
                           @NonNull List<DetailsResponse> detailsResponses,
                           @Nullable Location currentLocation
        ) {
            this.nearbyResponse = nearbyResponse;
            this.detailsResponses = detailsResponses;
            this.currentLocation = currentLocation;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListWrapper listWrapper = (ListWrapper) o;
            return Objects.equals(nearbyResponse, listWrapper.nearbyResponse) &&
                detailsResponses.equals(listWrapper.detailsResponses) &&
                Objects.equals(currentLocation, listWrapper.currentLocation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nearbyResponse, detailsResponses, currentLocation);
        }

        @NonNull
        @Override
        public String toString() {
            return "ListWrapper{" +
                "nearbyResponse=" + nearbyResponse +
                ", detailsResponses=" + detailsResponses +
                ", currentLocation=" + currentLocation +
                '}';
        }
    }
}
