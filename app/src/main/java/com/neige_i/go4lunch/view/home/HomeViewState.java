package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.List;
import java.util.Objects;

class HomeViewState {

    @StringRes
    private final int titleId;
    private final int viewPagerPosition;
    private final boolean isSearchEnabled;
    private final boolean isSearchResultListVisible;
    @NonNull
    private final List<AutocompleteViewState> autocompleteViewStates;

    HomeViewState(
        int titleId,
        int viewPagerPosition,
        boolean isSearchEnabled,
        boolean isSearchResultListVisible,
        @NonNull List<AutocompleteViewState> autocompleteViewStates
    ) {
        this.titleId = titleId;
        this.viewPagerPosition = viewPagerPosition;
        this.isSearchEnabled = isSearchEnabled;
        this.isSearchResultListVisible = isSearchResultListVisible;
        this.autocompleteViewStates = autocompleteViewStates;
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

    public boolean isSearchResultListVisible() {
        return isSearchResultListVisible;
    }

    @NonNull
    public List<AutocompleteViewState> getAutocompleteViewStates() {
        return autocompleteViewStates;
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
            isSearchResultListVisible == that.isSearchResultListVisible &&
            autocompleteViewStates.equals(that.autocompleteViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleId, viewPagerPosition, isSearchEnabled, isSearchResultListVisible, autocompleteViewStates);
    }

    @NonNull
    @Override
    public String toString() {
        return "HomeViewState{" +
            "titleId=" + titleId +
            ", viewPagerPosition=" + viewPagerPosition +
            ", isSearchEnabled=" + isSearchEnabled +
            ", isSearchResultListVisible=" + isSearchResultListVisible +
            ", autocompleteViewStates=" + autocompleteViewStates +
            '}';
    }
}
