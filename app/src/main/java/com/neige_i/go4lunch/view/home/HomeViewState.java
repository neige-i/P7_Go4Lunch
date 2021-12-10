package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;

import java.util.List;
import java.util.Objects;

class HomeViewState {

    @StringRes
    private final int titleId;
    private final int viewPagerPosition;
    private final boolean isSearchEnabled;
    @Nullable
    private final String searchQuery;
    @NonNull
    private final List<AutocompleteRestaurant> autocompleteResults;

    HomeViewState(
        int titleId,
        int viewPagerPosition,
        boolean isSearchEnabled,
        @Nullable String searchQuery,
        @NonNull List<AutocompleteRestaurant> autocompleteResults
    ) {
        this.titleId = titleId;
        this.viewPagerPosition = viewPagerPosition;
        this.isSearchEnabled = isSearchEnabled;
        this.searchQuery = searchQuery;
        this.autocompleteResults = autocompleteResults;
    }

    @StringRes
    public int getTitleId() {
        return titleId;
    }

    public int getViewPagerPosition() {
        return viewPagerPosition;
    }

    public boolean isSearchEnabled() {
        return isSearchEnabled;
    }

    @Nullable
    public String getSearchQuery() {
        return searchQuery;
    }

    @NonNull
    public List<AutocompleteRestaurant> getAutocompleteResults() {
        return autocompleteResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HomeViewState that = (HomeViewState) o;
        return titleId == that.titleId &&
            viewPagerPosition == that.viewPagerPosition &&
            isSearchEnabled == that.isSearchEnabled &&
            Objects.equals(searchQuery, that.searchQuery) &&
            autocompleteResults.equals(that.autocompleteResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleId, viewPagerPosition, isSearchEnabled, searchQuery, autocompleteResults);
    }

    @NonNull
    @Override
    public String toString() {
        return "HomeViewState{" +
            "titleId=" + titleId +
            ", viewPagerPosition=" + viewPagerPosition +
            ", isSearchEnabled=" + isSearchEnabled +
            ", searchQuery='" + searchQuery + '\'' +
            ", autocompleteResults=" + autocompleteResults +
            '}';
    }
}
