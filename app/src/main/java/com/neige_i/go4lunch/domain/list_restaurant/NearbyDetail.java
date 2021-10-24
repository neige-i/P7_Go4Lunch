package com.neige_i.go4lunch.domain.list_restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class NearbyDetail {

    @NonNull
    private final String placeId;
    @NonNull
    private final String restaurantName;
    @NonNull
    private final String address;
    private final float distance;
    private final int rating;
    @Nullable
    private final String photoUrl;
    @NonNull
    private final HourResult hourResult;
    private final int interestedWorkmatesCount;

    NearbyDetail(
        @NonNull String placeId,
        @NonNull String restaurantName,
        @NonNull String address,
        float distance,
        int rating,
        @Nullable String photoUrl,
        @NonNull HourResult hourResult,
        int interestedWorkmatesCount
    ) {
        this.placeId = placeId;
        this.restaurantName = restaurantName;
        this.address = address;
        this.distance = distance;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.hourResult = hourResult;
        this.interestedWorkmatesCount = interestedWorkmatesCount;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getRestaurantName() {
        return restaurantName;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public float getDistance() {
        return distance;
    }

    public int getRating() {
        return rating;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @NonNull
    public HourResult getHourResult() {
        return hourResult;
    }

    public int getInterestedWorkmatesCount() {
        return interestedWorkmatesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NearbyDetail that = (NearbyDetail) o;
        return Float.compare(that.distance, distance) == 0 &&
            rating == that.rating &&
            interestedWorkmatesCount == that.interestedWorkmatesCount &&
            placeId.equals(that.placeId) &&
            restaurantName.equals(that.restaurantName) &&
            address.equals(that.address) &&
            Objects.equals(photoUrl, that.photoUrl) &&
            Objects.equals(hourResult, that.hourResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, restaurantName, address, distance, rating, photoUrl, hourResult, interestedWorkmatesCount);
    }

    @NonNull
    @Override
    public String toString() {
        return "NearbyDetails{" +
            "placeId='" + placeId + '\'' +
            ", restaurantName='" + restaurantName + '\'' +
            ", address='" + address + '\'' +
            ", distanceToCurrentLocation=" + distance +
            ", rating=" + rating +
            ", photoUrl='" + photoUrl + '\'' +
            ", openingHours=" + hourResult +
            ", interestedWorkmatesCount=" + interestedWorkmatesCount +
            '}';
    }
}
