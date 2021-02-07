package com.neige_i.go4lunch.view.detail;

import java.util.Objects;

class DetailViewState {

    private final String placeId;
    private final String photoUrl;
    private final String name;
    private final String address;
    private final int rating;
    private final String phoneNumber;
    private final String website;
    private final boolean selected;
    private final boolean favorite;

    public DetailViewState(String placeId, String name, String photoUrl, String address, int rating, String phoneNumber, String website, boolean selected, boolean favorite) {
        this.placeId = placeId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.address = address;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.selected = selected;
        this.favorite = favorite;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRating() {
        return rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

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
            photoUrl.equals(that.photoUrl) &&
            name.equals(that.name) &&
            address.equals(that.address) &&
            phoneNumber.equals(that.phoneNumber) &&
            website.equals(that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, photoUrl, name, address, rating, phoneNumber, website, selected, favorite);
    }
}
