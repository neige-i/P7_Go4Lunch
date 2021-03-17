package com.neige_i.go4lunch.view.list_workmate;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.util.Objects;

class WorkmateViewState {

    /**
     * Used for the {@link WorkmateViewState#selectedRestaurantId} field because it is used inside a View's tag.<br />
     * And a null View tag is considered as not set.
     */
    static final String NO_SELECTED_RESTAURANT = "NO_SELECTED_RESTAURANT";

    @NonNull
    private final String workmateId;
    @NonNull
    private final String profileImageUrl;
    private final int textStyle;
    @IdRes
    private final int textColor;
    @NonNull
    private final String nameAndSelectedRestaurant;
    @NonNull
    private final String selectedRestaurantId;

    WorkmateViewState(@NonNull String workmateId, @NonNull String profileImageUrl, int textStyle, int textColor, @NonNull String nameAndSelectedRestaurant, @NonNull String selectedRestaurantId) {
        this.workmateId = workmateId;
        this.profileImageUrl = profileImageUrl;
        this.textStyle = textStyle;
        this.textColor = textColor;
        this.nameAndSelectedRestaurant = nameAndSelectedRestaurant;
        this.selectedRestaurantId = selectedRestaurantId;
    }

    @NonNull
    public String getWorkmateId() {
        return workmateId;
    }

    @NonNull
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public int getTextColor() {
        return textColor;
    }

    @NonNull
    public String getNameAndSelectedRestaurant() {
        return nameAndSelectedRestaurant;
    }

    @NonNull
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateViewState viewState = (WorkmateViewState) o;
        return textStyle == viewState.textStyle &&
            textColor == viewState.textColor &&
            workmateId.equals(viewState.workmateId) &&
            profileImageUrl.equals(viewState.profileImageUrl) &&
            nameAndSelectedRestaurant.equals(viewState.nameAndSelectedRestaurant) &&
            selectedRestaurantId.equals(viewState.selectedRestaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateId, profileImageUrl, textStyle, textColor, nameAndSelectedRestaurant, selectedRestaurantId);
    }
}
