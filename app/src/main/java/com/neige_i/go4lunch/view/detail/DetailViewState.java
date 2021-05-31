package com.neige_i.go4lunch.view.detail;

import java.util.List;
import java.util.Objects;

class DetailViewState {

    private final String placeId;
    private final String photoUrl;
    private final String name;
    private final String address;
    private final int rating;
    private final boolean noRatingLblVisibility;
    private final String phoneNumber;
    private final String website;
    private final boolean selected;
    private final boolean favorite;
    private final List<String> workmateIds;

    public DetailViewState(String placeId, String name, String photoUrl, String address, int rating, boolean noRatingLblVisibility, String phoneNumber, String website, boolean selected, boolean favorite, List<String> workmateIds) {
        this.placeId = placeId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.address = address;
        this.rating = rating;
        this.noRatingLblVisibility = noRatingLblVisibility;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.selected = selected;
        this.favorite = favorite;
        this.workmateIds = workmateIds;
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

    public boolean isNoRatingLblVisible() {
        return noRatingLblVisibility;
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

    public List<String> getWorkmateIds() {
        return workmateIds;
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
            website.equals(that.website) &&
            workmateIds.equals(that.workmateIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, photoUrl, name, address, rating, phoneNumber, website, selected, favorite, workmateIds);
    }
}
