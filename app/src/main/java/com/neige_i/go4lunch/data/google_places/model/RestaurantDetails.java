package com.neige_i.go4lunch.data.google_places.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class RestaurantDetails {

    @NonNull
    private final String placeId;
    @NonNull
    private final String name;
    @NonNull
    private final String address;
    private final int rating;
    @Nullable
    private final String photoUrl;
    @Nullable
    private final String phoneNumber;
    @Nullable
    private final String website;
    @Nullable
    private final List<String> openingHours;

    public RestaurantDetails(
        @NonNull String placeId,
        @NonNull String name,
        @NonNull String address,
        int rating,
        @Nullable String photoUrl,
        @Nullable String phoneNumber,
        @Nullable String website,
        @Nullable List<String> openingHours
    ) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.openingHours = openingHours;
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

    public int getRating() {
        return rating;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    @Nullable
    public List<String> getOpeningHours() {
        return openingHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestaurantDetails that = (RestaurantDetails) o;
        return rating == that.rating &&
            placeId.equals(that.placeId) &&
            name.equals(that.name) &&
            address.equals(that.address) &&
            Objects.equals(photoUrl, that.photoUrl) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(website, that.website) &&
            Objects.equals(openingHours, that.openingHours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, address, rating, photoUrl, phoneNumber, website, openingHours);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantDetails{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", rating=" + rating +
            ", photoUrl='" + photoUrl + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", website='" + website + '\'' +
            ", openingHours=" + openingHours +
            '}';
    }
}
