package com.neige_i.go4lunch.domain.map;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MapRestaurant {

    @NonNull
    private final String placeId;
    @NonNull
    private final String name;
    private final double latitude;
    private final double longitude;
    @NonNull
    private final String address;
    private final boolean searched;
    private final int interestedWorkmateCount;

    public MapRestaurant(
        @NonNull String placeId,
        @NonNull String name,
        double latitude,
        double longitude,
        @NonNull String address,
        boolean searched,
        int interestedWorkmateCount
    ) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.searched = searched;
        this.interestedWorkmateCount = interestedWorkmateCount;
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

    public boolean isSearched() {
        return searched;
    }

    public int getInterestedWorkmateCount() {
        return interestedWorkmateCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapRestaurant that = (MapRestaurant) o;
        return Double.compare(that.latitude, latitude) == 0 &&
            Double.compare(that.longitude, longitude) == 0 &&
            searched == that.searched &&
            interestedWorkmateCount == that.interestedWorkmateCount &&
            placeId.equals(that.placeId) &&
            name.equals(that.name) &&
            address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, latitude, longitude, address, searched, interestedWorkmateCount);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapRestaurant{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", address='" + address + '\'' +
            ", searched=" + searched +
            ", interestedWorkmateCount=" + interestedWorkmateCount +
            '}';
    }
}
