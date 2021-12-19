package com.neige_i.go4lunch.repository.google_places.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class NearbyRestaurant {

    @NonNull
    private final String placeId;
    @NonNull
    private final String name;
    @NonNull
    private final String address;
    private final double latitude;
    private final double longitude;
    private final int rating;
    @Nullable
    private final String photoUrl;

    public NearbyRestaurant(@NonNull String placeId, @NonNull String name, @NonNull String address, double latitude, double longitude, int rating, @Nullable String photoUrl) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.photoUrl = photoUrl;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRating() {
        return rating;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NearbyRestaurant that = (NearbyRestaurant) o;
        return Double.compare(that.latitude, latitude) == 0 && Double.compare(that.longitude, longitude) == 0 && rating == that.rating && placeId.equals(that.placeId) && name.equals(that.name) && address.equals(that.address) && Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, address, latitude, longitude, rating, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "NearbyRestaurant{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", rating=" + rating +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
