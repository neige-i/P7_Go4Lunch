package com.neige_i.go4lunch.view.list;

import androidx.annotation.IdRes;

import java.util.Objects;

class RestaurantViewState {

    private final String placeId;
    private final String name;
    private final int distance;
    private final String address;
    private final int textStyle;
    @IdRes
    private final int textColor;
    private final String openingHours;
    private final boolean interestedWorkmates;
    private final int interestedWorkmatesCount;
    private final int rating;
    private final String photoUrl;


    public RestaurantViewState(String placeId, String name, int distance, String address, int textStyle, int textColor, String openingHours, boolean interestedWorkmates, int interestedWorkmatesCount, int rating, String photoUrl) {
        this.placeId = placeId;
        this.name = name;
        this.distance = distance;
        this.address = address;
        this.textStyle = textStyle;
        this.textColor = textColor;
        this.openingHours = openingHours;
        this.interestedWorkmates = interestedWorkmates;
        this.interestedWorkmatesCount = interestedWorkmatesCount;
        this.rating = rating;
        this.photoUrl = photoUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public int getTextColor() {
        return textColor;
    }

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantViewState that = (RestaurantViewState) o;
        return textStyle == that.textStyle &&
            textColor == that.textColor &&
            interestedWorkmates == that.interestedWorkmates &&
            interestedWorkmatesCount == that.interestedWorkmatesCount &&
            rating == that.rating &&
            Objects.equals(placeId, that.placeId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(distance, that.distance) &&
            Objects.equals(address, that.address) &&
            Objects.equals(openingHours, that.openingHours) &&
            Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, distance, address, textStyle, textColor, openingHours, interestedWorkmates, interestedWorkmatesCount, rating, photoUrl);
    }
}
