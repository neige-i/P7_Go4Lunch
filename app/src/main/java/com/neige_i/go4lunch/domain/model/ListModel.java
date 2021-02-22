package com.neige_i.go4lunch.domain.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;

import java.util.List;
import java.util.Objects;

public class ListModel {

    @NonNull
    private final List<DetailsResponse> detailsResponses;
    @NonNull
    private final Location currentLocation;

    public ListModel(@NonNull List<DetailsResponse> detailsResponses, @Nullable Location currentLocation) {
        this.detailsResponses = detailsResponses;
        this.currentLocation = currentLocation;
    }

    @NonNull
    public List<DetailsResponse> getDetailsResponses() {
        return detailsResponses;
    }

    @NonNull
    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListModel listModel = (ListModel) o;
        return detailsResponses.equals(listModel.detailsResponses) &&
            currentLocation.equals(listModel.currentLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(detailsResponses, currentLocation);
    }

    @Override
    public String toString() {
        return "ListModel{" +
            "details=" + detailsResponses +
            ", location=" + currentLocation +
            '}';
    }
}
