package com.neige_i.go4lunch.view.detail;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

class DetailViewState {

    @NonNull
    private final String name;
    @Nullable
    private final String photoUrl;
    @NonNull
    private final String address;
    private final int rating;
    @Nullable
    private final String phoneNumber;
    @Nullable
    private final String website;
    private final boolean favorite;
    @DrawableRes
    private final int checkButtonDrawable;
    @ColorRes
    private final int checkButtonColor;
    @NonNull
    private final List<WorkmateViewState> workmateViewStates;

    DetailViewState(
        @NonNull String name,
        @Nullable String photoUrl,
        @NonNull String address,
        int rating,
        @Nullable String phoneNumber,
        @Nullable String website,
        boolean favorite,
        @DrawableRes int checkButtonDrawable,
        @ColorRes int checkButtonColor,
        @NonNull List<WorkmateViewState> workmateViewStates
    ) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.address = address;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.favorite = favorite;
        this.checkButtonDrawable = checkButtonDrawable;
        this.checkButtonColor = checkButtonColor;
        this.workmateViewStates = workmateViewStates;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @NonNull
    public String getAddress() {
        return address;
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

    public int getCheckButtonDrawable() {
        return checkButtonDrawable;
    }

    public int getCheckButtonColor() {
        return checkButtonColor;
    }

    @NonNull
    public List<WorkmateViewState> getWorkmateViewStates() {
        return workmateViewStates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DetailViewState that = (DetailViewState) o;
        return rating == that.rating &&
            favorite == that.favorite &&
            checkButtonDrawable == that.checkButtonDrawable &&
            checkButtonColor == that.checkButtonColor &&
            name.equals(that.name) &&
            Objects.equals(photoUrl, that.photoUrl) &&
            address.equals(that.address) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(website, that.website) &&
            workmateViewStates.equals(that.workmateViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, photoUrl, address, rating, phoneNumber, website, favorite, checkButtonDrawable, checkButtonColor, workmateViewStates);
    }
}
