package com.neige_i.go4lunch.view.map;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MarkerViewState {

    private final String placeId;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String vicinity;


    public MarkerViewState(String placeId, String name, double latitude, double longitude, String vicinity) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vicinity = vicinity;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getVicinity() {
        return vicinity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkerViewState that = (MarkerViewState) o;
        return Double.compare(that.latitude, latitude) == 0 &&
            Double.compare(that.longitude, longitude) == 0 &&
            Objects.equals(placeId, that.placeId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(vicinity, that.vicinity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, latitude, longitude, vicinity);
    }

    @NonNull
    @Override
    public String toString() {
        return "MarkerViewState{" +
            placeId +
            ": '" + name +
            "' " + latitude +
            "," + longitude +
            " '" + vicinity + '\'' +
            '}';
    }
}
