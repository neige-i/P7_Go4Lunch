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
    private final String selectedRestaurantId;
    @Nullable
    private final String selectedRestaurantName;

    /**
     * Mandatory empty constructor for Firestore.
     */
    public User() {
        id = null;
        email = null;
        name = null;
        photoUrl = null;
        selectedRestaurantId = null;
        selectedRestaurantName = null;
    }

    public User(@Nullable String id, @Nullable String email, @Nullable String name, @Nullable String photoUrl, @Nullable String selectedRestaurantId, @Nullable String selectedRestaurantName) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.selectedRestaurantId = selectedRestaurantId;
        this.selectedRestaurantName = selectedRestaurantName;
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
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }

    @Nullable
    public String getSelectedRestaurantName() {
        return selectedRestaurantName;
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
            Objects.equals(selectedRestaurantId, user.selectedRestaurantId) &&
            Objects.equals(selectedRestaurantName, user.selectedRestaurantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, photoUrl, selectedRestaurantId, selectedRestaurantName);
    }
}
