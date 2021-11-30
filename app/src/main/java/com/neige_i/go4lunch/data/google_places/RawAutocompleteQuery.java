package com.neige_i.go4lunch.data.google_places;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.Objects;

public class RawAutocompleteQuery {

    @NonNull
    private final String searchQuery;
    @NonNull
    private final Location location;

    public RawAutocompleteQuery(@NonNull String searchQuery, @NonNull Location location) {
        this.searchQuery = searchQuery;
        this.location = location;
    }

    @NonNull
    public String getSearchQuery() {
        return searchQuery;
    }

    @NonNull
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RawAutocompleteQuery that = (RawAutocompleteQuery) o;
        return searchQuery.equals(that.searchQuery) && location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchQuery, location);
    }

    @NonNull
    @Override
    public String toString() {
        return "RawAutocompleteQuery{" +
            "searchQuery='" + searchQuery + '\'' +
            ", location=" + location +
            '}';
    }
}
