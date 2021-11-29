package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;

import java.util.Objects;

class AutocompleteViewState {

    @NonNull
    private final String placeId;
    @NonNull
    private final String restaurantName;

    AutocompleteViewState(@NonNull String placeId, @NonNull String restaurantName) {
        this.placeId = placeId;
        this.restaurantName = restaurantName;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getRestaurantName() {
        return restaurantName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AutocompleteViewState that = (AutocompleteViewState) o;
        return placeId.equals(that.placeId) && restaurantName.equals(that.restaurantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, restaurantName);
    }

    @NonNull
    @Override
    public String toString() {
        return "AutocompleteViewState{" +
            "placeId='" + placeId + '\'' +
            ", restaurantName='" + restaurantName + '\'' +
            '}';
    }
}
