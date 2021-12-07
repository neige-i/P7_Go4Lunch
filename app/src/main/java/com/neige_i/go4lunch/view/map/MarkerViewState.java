package com.neige_i.go4lunch.view.map;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.Objects;

class MarkerViewState {

    @NonNull
    private final String placeId;
    @NonNull
    private final String name;
    private final double latitude;
    private final double longitude;
    @NonNull
    private final String address;
    @DrawableRes
    private final int markerDrawable;
    private final int size;

    MarkerViewState(
        @NonNull String placeId,
        @NonNull String name,
        double latitude,
        double longitude,
        @NonNull String address,
        @DrawableRes int markerDrawable,
        int size
    ) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.markerDrawable = markerDrawable;
        this.size = size;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    @DrawableRes
    public int getMarkerDrawable() {
        return markerDrawable;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MarkerViewState that = (MarkerViewState) o;
        return Double.compare(that.latitude, latitude) == 0 &&
            Double.compare(that.longitude, longitude) == 0 &&
            markerDrawable == that.markerDrawable &&
            size == that.size &&
            placeId.equals(that.placeId) &&
            name.equals(that.name) &&
            address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, latitude, longitude, address, markerDrawable, size);
    }

    @NonNull
    @Override
    public String toString() {
        return "MarkerViewState{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", address='" + address + '\'' +
            ", markerDrawable=" + markerDrawable +
            ", size=" + size +
            '}';
    }
}
