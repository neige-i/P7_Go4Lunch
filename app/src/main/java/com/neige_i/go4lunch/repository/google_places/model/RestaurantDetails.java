package com.neige_i.go4lunch.repository.google_places.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RestaurantDetails {

    @NonNull
    private final String placeId;
    @NonNull
    private final String name;
    @NonNull
    private final String address;
    private final double latitude;
    private final double longitude;
    private final int rating;
    @Nullable
    private final String photoUrl;
    @Nullable
    private final String phoneNumber;
    @Nullable
    private final String website;
    @NonNull
    private final List<RestaurantHour> openingPeriods;

    public RestaurantDetails(
        @NonNull String placeId,
        @NonNull String name,
        @NonNull String address,
        double latitude, double longitude, int rating,
        @Nullable String photoUrl,
        @Nullable String phoneNumber,
        @Nullable String website,
        @NonNull List<RestaurantHour> openingPeriods
    ) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.openingPeriods = openingPeriods;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRating() {
        return rating;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    @NonNull
    public List<RestaurantHour> getOpeningPeriods() {
        return openingPeriods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestaurantDetails that = (RestaurantDetails) o;
        return Double.compare(that.latitude, latitude) == 0 &&
            Double.compare(that.longitude, longitude) == 0 &&
            rating == that.rating &&
            placeId.equals(that.placeId) &&
            name.equals(that.name) &&
            address.equals(that.address) &&
            Objects.equals(photoUrl, that.photoUrl) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(website, that.website) &&
            openingPeriods.equals(that.openingPeriods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, address, latitude, longitude, rating, photoUrl, phoneNumber, website, openingPeriods);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantDetails{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", latitude='" + latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            ", rating=" + rating +
            ", photoUrl='" + (photoUrl != null ? "not null" : "null") + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", website='" + website + '\'' +
            ", openingPeriods=" + openingPeriods +
            '}';
    }

    public static class RestaurantHour {

        private final boolean open;
        @NonNull
        private final DayOfWeek dayOfWeek;
        @NonNull
        private final LocalTime localTime;

        public RestaurantHour(
            boolean open,
            @NonNull DayOfWeek dayOfWeek,
            @NonNull LocalTime localTime
        ) {
            this.open = open;
            this.dayOfWeek = dayOfWeek;
            this.localTime = localTime;
        }

        public boolean isOpen() {
            return open;
        }

        @NonNull
        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        @NonNull
        public LocalTime getLocalTime() {
            return localTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RestaurantHour that = (RestaurantHour) o;
            return open == that.open &&
                dayOfWeek == that.dayOfWeek &&
                localTime.equals(that.localTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(open, dayOfWeek, localTime);
        }

        @NonNull
        @Override
        public String toString() {
            return "{" +
                (open ? "opens" : "closes") +
                " " + dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()) +
                " at " + localTime +
                '}';
        }
    }
}
