package com.neige_i.go4lunch.data.firebase.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class User {

    @Nullable
    private final String id;
    @Nullable
    private final String email;
    @Nullable
    private final String name;
    @Nullable
    private final String photoUrl;
    @Nullable
    private final SelectedRestaurant selectedRestaurant;

    /**
     * Mandatory empty constructor for Firestore.
     */
    public User() {
        id = null;
        email = null;
        name = null;
        photoUrl = null;
        selectedRestaurant = null;
    }

    public User(@Nullable String id, @Nullable String email, @Nullable String name, @Nullable String photoUrl, @Nullable SelectedRestaurant selectedRestaurant) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.selectedRestaurant = selectedRestaurant;
    }

    @Nullable
    public String getId() {
        return id;
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
    public SelectedRestaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
            Objects.equals(email, user.email) &&
            Objects.equals(name, user.name) &&
            Objects.equals(photoUrl, user.photoUrl) &&
            Objects.equals(selectedRestaurant, user.selectedRestaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, photoUrl, selectedRestaurant);
    }

    public static class SelectedRestaurant {

        @Nullable private final String restaurantId;
        @Nullable private final String restaurantName;
        private final long selectedDate;

        public SelectedRestaurant() {
            restaurantId = null;
            restaurantName = null;
            selectedDate = 0;
        }

        public SelectedRestaurant(@Nullable String restaurantId, @Nullable String restaurantName, long selectedDate) {
            this.restaurantId = restaurantId;
            this.restaurantName = restaurantName;
            this.selectedDate = selectedDate;
        }

        @Nullable
        public String getRestaurantId() {
            return restaurantId;
        }

        @Nullable
        public String getRestaurantName() {
            return restaurantName;
        }

        public long getSelectedDate() {
            return selectedDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SelectedRestaurant that = (SelectedRestaurant) o;
            return selectedDate == that.selectedDate &&
                Objects.equals(restaurantId, that.restaurantId) &&
                Objects.equals(restaurantName, that.restaurantName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(restaurantId, restaurantName, selectedDate);
        }
    }
}
