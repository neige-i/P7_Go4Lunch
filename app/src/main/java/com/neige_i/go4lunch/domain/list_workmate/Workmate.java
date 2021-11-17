package com.neige_i.go4lunch.domain.list_workmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public abstract class Workmate {

    @NonNull
    private final String email;
    @NonNull
    private final String name;
    @Nullable
    private final String photoUrl;
    private final boolean isCurrentUser;

    private Workmate(
        @NonNull String email,
        @NonNull String name,
        @Nullable String photoUrl,
        boolean isCurrentUser
    ) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.isCurrentUser = isCurrentUser;
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

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Workmate workmate = (Workmate) o;
        return isCurrentUser == workmate.isCurrentUser && email.equals(workmate.email) && name.equals(workmate.name) && Objects.equals(photoUrl, workmate.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, photoUrl, isCurrentUser);
    }

    @NonNull
    @Override
    public String toString() {
        return "Workmate{" +
            "email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", isCurrentUser=" + isCurrentUser +
            '}';
    }

    public static class WithoutRestaurant extends Workmate {

        public WithoutRestaurant(
            @NonNull String email,
            @NonNull String name,
            @Nullable String photoUrl,
            boolean isCurrentUser
        ) {
            super(email, name, photoUrl, isCurrentUser);
        }
    }

    public static class WithRestaurant extends Workmate {

        @NonNull
        private final String restaurantId;
        @NonNull
        private final String restaurantName;

        public WithRestaurant(
            @NonNull String email,
            @NonNull String name,
            @Nullable String photoUrl,
            boolean isCurrentUser,
            @NonNull String restaurantId,
            @NonNull String restaurantName
        ) {
            super(email, name, photoUrl, isCurrentUser);
            this.restaurantId = restaurantId;
            this.restaurantName = restaurantName;
        }

        @NonNull
        public String getRestaurantId() {
            return restaurantId;
        }

        @NonNull
        public String getRestaurantName() {
            return restaurantName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            WithRestaurant that = (WithRestaurant) o;
            return restaurantId.equals(that.restaurantId) && restaurantName.equals(that.restaurantName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), restaurantId, restaurantName);
        }

        @NonNull
        @Override
        public String toString() {
            return "WithRestaurant{" +
                "restaurantId='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                '}';
        }
    }
}
