package com.neige_i.go4lunch.data.firebase.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Objects;

public class Restaurant {

    @Nullable
    private final String restaurantId;
    @Nullable
    private final Map<String, Long> workmateMap;

    /**
     * Mandatory empty constructor for Firestore.
     */
    public Restaurant() {
        restaurantId = null;
        workmateMap = null;
    }

    public Restaurant(@Nullable String restaurantId, @Nullable Map<String, Long> workmateMap) {
        this.restaurantId = restaurantId;
        this.workmateMap = workmateMap;
    }

    @Nullable
    public String getRestaurantId() {
        return restaurantId;
    }

    @Nullable
    public Map<String, Long> getWorkmateMap() {
        return workmateMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Restaurant that = (Restaurant) o;
        return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(workmateMap, that.workmateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, workmateMap);
    }

    @NonNull
    @Override
    public String toString() {
        return "Restaurant{" +
            "restaurantId='" + restaurantId + '\'' +
            ", workmateMap=" + workmateMap +
            '}';
    }
}
