package com.neige_i.go4lunch.view.list_restaurant;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

class RestaurantViewState {

    @NonNull
    private final String placeId;
    @NonNull
    private final String name;
    private final float distance;
    @NonNull
    private final String formattedDistance;
    @NonNull
    private final String address;
    private final int textStyle;
    @IdRes
    private final int textColor;
    @NonNull
    private final String openingHours;
    private final int interestedWorkmatesCount;
    private final int rating;
    @Nullable
    private final String photoUrl;


    public RestaurantViewState(
        @NonNull String placeId,
        @NonNull String name,
        float distance,
        @NonNull String formattedDistance,
        @NonNull String address,
        int textStyle,
        int textColor,
        @NonNull String openingHours,
        int interestedWorkmatesCount,
        int rating,
        @Nullable String photoUrl
    ) {
        this.placeId = placeId;
        this.name = name;
        this.distance = distance;
        this.formattedDistance = formattedDistance;
        this.address = address;
        this.textStyle = textStyle;
        this.textColor = textColor;
        this.openingHours = openingHours;
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

    public float getDistance() {
        return distance;
    }

    @NonNull
    public String getFormattedDistance() {
        return formattedDistance;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public int getTextColor() {
        return textColor;
    }

    @NonNull
    public String getOpeningHours() {
        return openingHours;
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
            formattedDistance.equals(that.formattedDistance) &&
            address.equals(that.address) &&
            openingHours.equals(that.openingHours) &&
            Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, distance, formattedDistance, address, textStyle, textColor, openingHours, interestedWorkmatesCount, rating, photoUrl);
    }
}
