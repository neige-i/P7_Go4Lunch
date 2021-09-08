package com.neige_i.go4lunch.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.DetailsRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;

import java.util.List;
import java.util.Objects;

public class DetailsModel {

    @Nullable
    private final DetailsRestaurant detailsRestaurant;
    @Nullable
    private final String selectedRestaurant;
    @NonNull
    private final List<String> favoriteRestaurants;

    public DetailsModel(@Nullable DetailsRestaurant detailsRestaurant, @Nullable String selectedRestaurant, @NonNull List<String> favoriteRestaurants) {
        this.detailsRestaurant = detailsRestaurant;
        this.selectedRestaurant = selectedRestaurant;
        this.favoriteRestaurants = favoriteRestaurants;
    }

    @Nullable
    public DetailsRestaurant getDetailsResponse() {
        return detailsRestaurant;
    }

    @Nullable
    public String getSelectedRestaurant() {
        return selectedRestaurant;
    }

    @NonNull
    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailsModel that = (DetailsModel) o;
        return Objects.equals(selectedRestaurant, that.selectedRestaurant) &&
            favoriteRestaurants.equals(that.favoriteRestaurants) &&
            Objects.equals(detailsRestaurant, that.detailsRestaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(detailsRestaurant, selectedRestaurant, favoriteRestaurants);
    }
}
