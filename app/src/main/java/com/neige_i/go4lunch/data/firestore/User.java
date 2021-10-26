package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class User {

    @Nullable
    private final String email;
    @Nullable
    private final String name;
    @Nullable
    private final String photoUrl;
    @Nullable
    private final String selectedRestaurantId;
    @Nullable
    private final String selectedRestaurantName;
    @Nullable
    private final String selectedRestaurantDate;
    @Nullable
    private final List<String> favoriteRestaurants;

    /**
     * Mandatory empty constructor for Firestore.
     */
    public User() {
        email = null;
        name = null;
        photoUrl = null;
        selectedRestaurantId = null;
        selectedRestaurantName = null;
        selectedRestaurantDate = null;
        favoriteRestaurants = null;
    }

    public User(
        @Nullable String email,
        @Nullable String name,
        @Nullable String photoUrl,
        @Nullable String selectedRestaurantId,
        @Nullable String selectedRestaurantName,
        @Nullable String selectedRestaurantDate,
        @Nullable List<String> favoriteRestaurants
    ) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.selectedRestaurantId = selectedRestaurantId;
        this.selectedRestaurantName = selectedRestaurantName;
        this.selectedRestaurantDate = selectedRestaurantDate;
        this.favoriteRestaurants = favoriteRestaurants;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Nullable
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }

    @Nullable
    public String getSelectedRestaurantName() {
        return selectedRestaurantName;
    }

    @Nullable
    public String getSelectedRestaurantDate() {
        return selectedRestaurantDate;
    }

    @Nullable
    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(email, user.email) &&
            Objects.equals(name, user.name) &&
            Objects.equals(photoUrl, user.photoUrl) &&
            Objects.equals(selectedRestaurantId, user.selectedRestaurantId) &&
            Objects.equals(selectedRestaurantName, user.selectedRestaurantName) &&
            Objects.equals(selectedRestaurantDate, user.selectedRestaurantDate) &&
            Objects.equals(favoriteRestaurants, user.favoriteRestaurants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, photoUrl, selectedRestaurantId, selectedRestaurantName, selectedRestaurantDate, favoriteRestaurants);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
            "email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", photoUrl='" + (photoUrl != null ? "not null" : "null") + '\'' +
            ", selectedRestaurantId='" + selectedRestaurantId + '\'' +
            ", selectedRestaurantName='" + selectedRestaurantName + '\'' +
            ", selectedRestaurantDate='" + selectedRestaurantDate + '\'' +
            ", favoriteRestaurants=" + favoriteRestaurants +
            '}';
    }
}
