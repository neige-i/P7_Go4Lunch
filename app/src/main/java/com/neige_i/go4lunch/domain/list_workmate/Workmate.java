package com.neige_i.go4lunch.domain.list_workmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class Workmate {

    @NonNull
    private final String email;
    @NonNull
    private final String name;
    @Nullable
    private final String photoUrl;

    protected Workmate(@NonNull String email, @NonNull String name, @Nullable String photoUrl) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
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

    public static class WithoutRestaurant extends Workmate {

        public WithoutRestaurant(
            @NonNull String email,
            @NonNull String name,
            @Nullable String photoUrl
        ) {
            super(email, name, photoUrl);
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
            @NonNull String restaurantId,
            @NonNull String restaurantName
        ) {
            super(email, name, photoUrl);
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
    }
}
