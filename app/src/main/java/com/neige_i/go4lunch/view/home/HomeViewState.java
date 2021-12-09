package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

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
    private final List<String> autocompleteResults;
    @Nullable
    private final String userProfilePhoto;
    @NonNull
    private final String username;
    @NonNull
    private final String userEmail;
    @Nullable
    private final String selectedRestaurantId;

    HomeViewState(
        int titleId,
        int viewPagerPosition,
        boolean isSearchEnabled,
        @Nullable String searchQuery,
        @NonNull List<String> autocompleteResults,
        @Nullable String userProfilePhoto,
        @NonNull String username,
        @NonNull String userEmail,
        @Nullable String selectedRestaurantId
    ) {
        this.titleId = titleId;
        this.viewPagerPosition = viewPagerPosition;
        this.isSearchEnabled = isSearchEnabled;
        this.searchQuery = searchQuery;
        this.autocompleteResults = autocompleteResults;
        this.userProfilePhoto = userProfilePhoto;
        this.username = username;
        this.userEmail = userEmail;
        this.selectedRestaurantId = selectedRestaurantId;
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
    public List<String> getAutocompleteResults() {
        return autocompleteResults;
    }

    @Nullable
    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getUserEmail() {
        return userEmail;
    }

    @Nullable
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
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
            autocompleteResults.equals(that.autocompleteResults) &&
            Objects.equals(userProfilePhoto, that.userProfilePhoto) &&
            username.equals(that.username) && userEmail.equals(that.userEmail) &&
            Objects.equals(selectedRestaurantId, that.selectedRestaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleId, viewPagerPosition, isSearchEnabled, searchQuery, autocompleteResults, userProfilePhoto, username, userEmail, selectedRestaurantId);
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
            ", userProfilePhoto='" + userProfilePhoto + '\'' +
            ", username='" + username + '\'' +
            ", userEmail='" + userEmail + '\'' +
            ", selectedRestaurantId='" + selectedRestaurantId + '\'' +
            '}';
    }
}
