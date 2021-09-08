package com.neige_i.go4lunch.view.list_restaurant;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

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
    private final boolean interestedWorkmates;
    private final int interestedWorkmatesCount;
    private final int rating;
    private final boolean noRatingLblVisibility;
    @NonNull
    private final String photoUrl;


    public RestaurantViewState(@NonNull String placeId, @NonNull String name, float distance, @NonNull String formattedDistance, @NonNull String address, int textStyle, int textColor, @NonNull String openingHours, boolean interestedWorkmates, int interestedWorkmatesCount, int rating, boolean noRatingLblVisibility, @NonNull String photoUrl) {
        this.placeId = placeId;
        this.name = name;
        this.distance = distance;
        this.formattedDistance = formattedDistance;
        this.address = address;
        this.textStyle = textStyle;
        this.textColor = textColor;
        this.openingHours = openingHours;
        this.interestedWorkmates = interestedWorkmates;
        this.interestedWorkmatesCount = interestedWorkmatesCount;
        this.rating = rating;
        this.noRatingLblVisibility = noRatingLblVisibility;
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

    public boolean areWorkmatesInterested() {
        return interestedWorkmates;
    }

    public int getInterestedWorkmatesCount() {
        return interestedWorkmatesCount;
    }

    public int getRating() {
        return rating;
    }

    public boolean isNoRatingLblVisible() {
        return noRatingLblVisibility;
    }

    @NonNull
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
            interestedWorkmates == that.interestedWorkmates &&
            interestedWorkmatesCount == that.interestedWorkmatesCount &&
            rating == that.rating &&
            noRatingLblVisibility == that.noRatingLblVisibility &&
            placeId.equals(that.placeId) &&
            name.equals(that.name) &&
            formattedDistance.equals(that.formattedDistance) &&
            address.equals(that.address) &&
            openingHours.equals(that.openingHours) &&
            photoUrl.equals(that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, distance, formattedDistance, address, textStyle, textColor, openingHours, interestedWorkmates, interestedWorkmatesCount, rating, noRatingLblVisibility, photoUrl);
    }
}
