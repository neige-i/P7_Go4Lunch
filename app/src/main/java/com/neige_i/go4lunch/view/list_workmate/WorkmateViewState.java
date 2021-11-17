package com.neige_i.go4lunch.view.list_workmate;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

class WorkmateViewState {

    @NonNull
    private final String workmateId;
    @Nullable
    private final String profileImageUrl;
    private final int textStyle;
    @ColorRes
    private final int textColor;
    @NonNull
    private final String text;
    @Nullable
    private final String selectedRestaurantId;

    WorkmateViewState(
        @NonNull String workmateId,
        @Nullable String profileImageUrl,
        int textStyle,
        @ColorRes int textColor,
        @NonNull String text,
        @Nullable String selectedRestaurantId
    ) {
        this.workmateId = workmateId;
        this.profileImageUrl = profileImageUrl;
        this.textStyle = textStyle;
        this.textColor = textColor;
        this.text = text;
        this.selectedRestaurantId = selectedRestaurantId;
    }

    @NonNull
    public String getWorkmateId() {
        return workmateId;
    }

    @Nullable
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getTextStyle() {
        return textStyle;
    }

    @ColorRes
    public int getTextColor() {
        return textColor;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @Nullable
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkmateViewState viewState = (WorkmateViewState) o;
        return textStyle == viewState.textStyle &&
            textColor == viewState.textColor &&
            workmateId.equals(viewState.workmateId) &&
            Objects.equals(profileImageUrl, viewState.profileImageUrl) &&
            text.equals(viewState.text) &&
            Objects.equals(selectedRestaurantId, viewState.selectedRestaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateId, profileImageUrl, textStyle, textColor, text, selectedRestaurantId);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkmateViewState{" +
            "workmateId='" + workmateId + '\'' +
            ", profileImageUrl='" + profileImageUrl + '\'' +
            ", textStyle=" + textStyle +
            ", textColor=" + textColor +
            ", text='" + text + '\'' +
            ", selectedRestaurantId='" + selectedRestaurantId + '\'' +
            '}';
    }
}
