package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class User {

    @NonNull
    private final String email;
    @NonNull
    private final String name;
    @Nullable
    private final String photoUrl;
    @Nullable
    private final SelectedRestaurant selectedRestaurant;
    @Nullable
    private final List<String> favoriteRestaurants;

    /**
     * Mandatory empty constructor for Firestore.
     */
    @SuppressWarnings("ConstantConditions")
    public User() {
        email = null;
        name = null;
        photoUrl = null;
        selectedRestaurant = null;
        favoriteRestaurants = null;
    }

    public User(
        @NonNull String email,
        @NonNull String name,
        @Nullable String photoUrl,
        @Nullable SelectedRestaurant selectedRestaurant,
        @Nullable List<String> favoriteRestaurants
    ) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.selectedRestaurant = selectedRestaurant;
        this.favoriteRestaurants = favoriteRestaurants;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
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
        return email.equals(user.email) &&
            name.equals(user.name) &&
            Objects.equals(photoUrl, user.photoUrl) &&
            Objects.equals(selectedRestaurant, user.selectedRestaurant) &&
            Objects.equals(favoriteRestaurants, user.favoriteRestaurants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, photoUrl, selectedRestaurant, favoriteRestaurants);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
            "email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", photoUrl='" + (photoUrl != null ? "not null" : "null") + '\'' +
            ", selectedRestaurant=" + selectedRestaurant +
            ", favoriteRestaurants=" + favoriteRestaurants +
            '}';
    }

    public static class SelectedRestaurant {

        @NonNull
        private final String id;
        @NonNull
        private final String date;
        @NonNull
        private final String name;

        @SuppressWarnings("ConstantConditions")
        public SelectedRestaurant() {
            id = null;
            date = null;
            name = null;
        }

        SelectedRestaurant(@NonNull String id, @NonNull String date, @NonNull String name) {
            this.id = id;
            this.date = date;
            this.name = name;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getDate() {
            return date;
        }

        @NonNull
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SelectedRestaurant that = (SelectedRestaurant) o;
            return id.equals(that.id) &&
                date.equals(that.date) &&
                name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, date, name);
        }

        @NonNull
        @Override
        public String toString() {
            return "SelectedRestaurant{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                '}';
        }
    }
}
