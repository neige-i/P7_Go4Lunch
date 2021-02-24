package com.neige_i.go4lunch.domain.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.List;
import java.util.Objects;

public class ListModel {

    @NonNull
    private final NearbyResponse nearbyResponse;
    @NonNull
    private final List<DetailsResponse> detailsResponses;
    @Nullable
    private final Location currentLocation;

    public ListModel(@NonNull NearbyResponse nearbyResponse,
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
        ListModel listModel = (ListModel) o;
        return Objects.equals(nearbyResponse, listModel.nearbyResponse) &&
            detailsResponses.equals(listModel.detailsResponses) &&
            Objects.equals(currentLocation, listModel.currentLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nearbyResponse, detailsResponses, currentLocation);
    }

    @Override
    public String toString() {
        return "ListModel{" +
            "nearbyResponse=" + nearbyResponse +
            ", detailsResponses=" + detailsResponses +
            ", currentLocation=" + currentLocation +
            '}';
    }
}
