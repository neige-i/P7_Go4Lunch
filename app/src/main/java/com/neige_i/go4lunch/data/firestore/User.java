package com.neige_i.go4lunch.data.firestore;

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
    private final String selectedRestaurant;
    private final long selectedDate;
    @Nullable
    private final List<String> favoriteRestaurants;

    /**
     * Mandatory empty constructor for Firestore.
     */
    public User() {
        email = null;
        name = null;
        photoUrl = null;
        selectedRestaurant = null;
        selectedDate = 0;
        favoriteRestaurants = null;
    }

    public User(
        @Nullable String email,
        @Nullable String name,
        @Nullable String photoUrl,
        @Nullable String selectedRestaurant,
        long selectedDate,
        @Nullable List<String> favoriteRestaurants
    ) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.selectedRestaurant = selectedRestaurant;
        this.selectedDate = selectedDate;
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
    public String getSelectedRestaurant() {
        return selectedRestaurant;
    }

    public long getSelectedDate() {
        return selectedDate;
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
        return selectedDate == user.selectedDate &&
            Objects.equals(email, user.email) &&
            Objects.equals(name, user.name) &&
            Objects.equals(photoUrl, user.photoUrl) &&
            Objects.equals(selectedRestaurant, user.selectedRestaurant) &&
            Objects.equals(favoriteRestaurants, user.favoriteRestaurants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, photoUrl, selectedRestaurant, selectedDate, favoriteRestaurants);
    }
}
