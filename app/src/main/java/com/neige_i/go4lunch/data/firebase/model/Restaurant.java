package com.neige_i.go4lunch.data.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class Restaurant {

    @Nullable
    private final String restaurantId;
    @Nullable
    private final String workmateId;
//    @Nullable
//    private final List<InterestedWorkmate> interestedWorkmates;

    /**
     * Mandatory empty constructor for Firestore.
     */
    public Restaurant() {
        restaurantId = null;
        workmateId = null;
    }

    public Restaurant(@Nullable String restaurantId/*, @Nullable List<InterestedWorkmate> interestedWorkmates*/, @Nullable String workmateId) {
        this.restaurantId = restaurantId;
        this.workmateId = workmateId;
//        this.interestedWorkmates = interestedWorkmates;
    }

    private Restaurant(Parcel in) {
        restaurantId = in.readString();
        workmateId = in.readString();
    }

    @Nullable
    public String getRestaurantId() {
        return restaurantId;
    }

//    @Nullable
//    public List<InterestedWorkmate> getInterestedWorkmates() {
//        return interestedWorkmates;
//    }

    @Nullable
    public String getWorkmateId() {
        return workmateId;
    }

    /**
     * Represents a snippet of the {@link User} class.
     */
    public class InterestedWorkmate {

        @Nullable
        private final String userId;
        @Nullable
        private final String name;
        @Nullable
        private final String photoUrl;

        public InterestedWorkmate() {
            userId = null;
            name = null;
            photoUrl = null;
        }

        private InterestedWorkmate(@Nullable String userId, @Nullable String name, @Nullable String photoUrl) {
            this.userId = userId;
            this.name = name;
            this.photoUrl = photoUrl;
        }

        @Nullable
        public String getUserId() {
            return userId;
        }

        @Nullable
        public String getName() {
            return name;
        }

        @Nullable
        public String getPhotoUrl() {
            return photoUrl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InterestedWorkmate that = (InterestedWorkmate) o;
            return Objects.equals(userId, that.userId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(photoUrl, that.photoUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, name, photoUrl);
        }
    }
}
