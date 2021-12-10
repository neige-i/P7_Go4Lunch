package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class DrawerInfo {

    @Nullable
    private final String photoUrl;
    @NonNull
    private final String username;
    @NonNull
    private final String userEmail;
    @Nullable
    private final String selectedRestaurantId;

    public DrawerInfo(
        @Nullable String photoUrl, @NonNull String username,
        @NonNull String userEmail,
        @Nullable String selectedRestaurantId
    ) {
        this.photoUrl = photoUrl;
        this.username = username;
        this.userEmail = userEmail;
        this.selectedRestaurantId = selectedRestaurantId;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
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
        DrawerInfo that = (DrawerInfo) o;
        return Objects.equals(photoUrl, that.photoUrl) &&
            username.equals(that.username) &&
            userEmail.equals(that.userEmail) &&
            Objects.equals(selectedRestaurantId, that.selectedRestaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, username, userEmail, selectedRestaurantId);
    }

    @NonNull
    @Override
    public String toString() {
        return "DrawerInfo{" +
            "photoUrl='" + photoUrl + '\'' +
            ", username='" + username + '\'' +
            ", userEmail='" + userEmail + '\'' +
            ", selectedRestaurantId='" + selectedRestaurantId + '\'' +
            '}';
    }
}
