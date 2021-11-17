package com.neige_i.go4lunch.domain.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class RestaurantInfo {

    @NonNull
    private final String name;
    @NonNull
    private final String address;
    @Nullable
    private final String photoUrl;
    private final int rating;
    @Nullable
    private final String phoneNumber;
    @Nullable
    private final String website;
    private final boolean favorite;
    private final boolean selected;
    @NonNull
    private final List<CleanWorkmate> interestedWorkmates;

    public RestaurantInfo(
        @NonNull String name,
        @NonNull String address,
        @Nullable String photoUrl,
        int rating,
        @Nullable String phoneNumber,
        @Nullable String website,
        boolean favorite,
        boolean selected,
        @NonNull List<CleanWorkmate> interestedWorkmates
    ) {
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.favorite = favorite;
        this.selected = selected;
        this.interestedWorkmates = interestedWorkmates;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getRating() {
        return rating;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isSelected() {
        return selected;
    }

    @NonNull
    public List<CleanWorkmate> getInterestedWorkmates() {
        return interestedWorkmates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestaurantInfo that = (RestaurantInfo) o;
        return rating == that.rating &&
            favorite == that.favorite &&
            selected == that.selected &&
            name.equals(that.name) &&
            address.equals(that.address) &&
            Objects.equals(photoUrl, that.photoUrl) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(website, that.website) &&
            interestedWorkmates.equals(that.interestedWorkmates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, photoUrl, rating, phoneNumber, website, favorite, selected, interestedWorkmates);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantInfo{" +
            "name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", rating=" + rating +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", website='" + website + '\'' +
            ", favorite=" + favorite +
            ", selected=" + selected +
            ", interestedWorkmates=" + interestedWorkmates +
            '}';
    }
}
