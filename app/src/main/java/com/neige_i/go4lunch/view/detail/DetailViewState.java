package com.neige_i.go4lunch.view.detail;

import androidx.annotation.NonNull;

import java.util.Objects;

class DetailViewState {

    @NonNull
    private final String placeId;
    @NonNull
    private final String image;
    @NonNull
    private final String name;
    @NonNull
    private final String address;
    private final int rating;
    @NonNull
    private final String phoneNumber;
    @NonNull
    private final String website;
    private final boolean selected;
    private final boolean favorite;

    public DetailViewState(@NonNull String placeId, @NonNull String name, @NonNull String image, @NonNull String address, int rating, @NonNull String phoneNumber, @NonNull String website, boolean selected, boolean favorite) {
        this.placeId = placeId;
        this.name = name;
        this.image = image;
        this.address = address;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.selected = selected;
        this.favorite = favorite;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getImage() {
        return image;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public int getRating() {
        return rating;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @NonNull
    public String getWebsite() {
        return website;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailViewState that = (DetailViewState) o;
        return rating == that.rating &&
            selected == that.selected &&
            favorite == that.favorite &&
            placeId.equals(that.placeId) &&
            image.equals(that.image) &&
            name.equals(that.name) &&
            address.equals(that.address) &&
            phoneNumber.equals(that.phoneNumber) &&
            website.equals(that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, image, name, address, rating, phoneNumber, website, selected, favorite);
    }
}
