package com.neige_i.go4lunch.view.list_restaurant;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

class RestaurantViewState {

    @NonNull
    private final String placeId;
    @NonNull
    private final String name;
    @NonNull
    private final String address;
    private final float distance;
    @NonNull
    private final String formattedDistance;
    @NonNull
    private final String openingHours;
    private final int textStyle;
    @ColorRes
    private final int textColor;
    private final int interestedWorkmatesCount;
    private final int rating;
    @Nullable
    private final String photoUrl;

    RestaurantViewState(
        @NonNull String placeId,
        @NonNull String name,
        @NonNull String address,
        float distance,
        @NonNull String formattedDistance,
        @NonNull String openingHours,
        int textStyle,
        @ColorRes int textColor,
        int interestedWorkmatesCount,
        int rating,
        @Nullable String photoUrl
    ) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.formattedDistance = formattedDistance;
        this.openingHours = openingHours;
        this.textStyle = textStyle;
        this.textColor = textColor;
        this.interestedWorkmatesCount = interestedWorkmatesCount;
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

    public float getDistance() {
        return distance;
    }

    @NonNull
    public String getFormattedDistance() {
        return formattedDistance;
    }

    @NonNull
    public String getOpeningHours() {
        return openingHours;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public int getTextColor() {
        return textColor;
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
        RestaurantViewState that = (RestaurantViewState) o;
        return Float.compare(that.distance, distance) == 0 &&
            textStyle == that.textStyle &&
            textColor == that.textColor &&
            interestedWorkmatesCount == that.interestedWorkmatesCount &&
            rating == that.rating &&
            placeId.equals(that.placeId) &&
            name.equals(that.name) &&
            address.equals(that.address) &&
            formattedDistance.equals(that.formattedDistance) &&
            openingHours.equals(that.openingHours) &&
            Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, address, distance, formattedDistance, openingHours, textStyle, textColor, interestedWorkmatesCount, rating, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantViewState{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", distance=" + distance +
            ", formattedDistance='" + formattedDistance + '\'' +
            ", openingHours='" + openingHours + '\'' +
            ", textStyle=" + textStyle +
            ", textColor=" + textColor +
            ", interestedWorkmatesCount=" + interestedWorkmatesCount +
            ", rating=" + rating +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
