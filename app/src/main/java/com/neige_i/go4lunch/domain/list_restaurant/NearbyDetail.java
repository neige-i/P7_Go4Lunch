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
    @NonNull
    private final HourResult hourResult;
    private final int interestedWorkmatesCount;
    private final int rating;
    @Nullable
    private final String photoUrl;

    public NearbyDetail(
        @NonNull String placeId,
        @NonNull String restaurantName,
        @NonNull String address,
        float distance,
        @NonNull HourResult hourResult,
        int interestedWorkmatesCount,
        int rating,
        @Nullable String photoUrl
    ) {
        this.placeId = placeId;
        this.restaurantName = restaurantName;
        this.address = address;
        this.distance = distance;
        this.hourResult = hourResult;
        this.interestedWorkmatesCount = interestedWorkmatesCount;
        this.rating = rating;
        this.photoUrl = photoUrl;
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

    @NonNull
    public HourResult getHourResult() {
        return hourResult;
    }

    public int getInterestedWorkmatesCount() {
        return interestedWorkmatesCount;
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
        NearbyDetail that = (NearbyDetail) o;
        return Float.compare(that.distance, distance) == 0 &&
            interestedWorkmatesCount == that.interestedWorkmatesCount &&
            rating == that.rating && placeId.equals(that.placeId) &&
            restaurantName.equals(that.restaurantName) &&
            address.equals(that.address) &&
            hourResult.equals(that.hourResult) &&
            Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, restaurantName, address, distance, hourResult, interestedWorkmatesCount, rating, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "NearbyDetail{" +
            "placeId='" + placeId + '\'' +
            ", restaurantName='" + restaurantName + '\'' +
            ", address='" + address + '\'' +
            ", distance=" + distance +
            ", hourResult=" + hourResult +
            ", interestedWorkmatesCount=" + interestedWorkmatesCount +
            ", rating=" + rating +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
