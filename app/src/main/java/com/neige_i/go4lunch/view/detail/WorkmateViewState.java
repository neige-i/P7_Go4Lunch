package com.neige_i.go4lunch.view.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

class WorkmateViewState {

    @NonNull
    private final String email;
    @NonNull
    private final String text;
    @Nullable
    private final String photoUrl;

    WorkmateViewState(@NonNull String email, @NonNull String text, @Nullable String photoUrl) {
        this.email = email;
        this.text = text;
        this.photoUrl = photoUrl;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkmateViewState that = (WorkmateViewState) o;
        return email.equals(that.email) &&
            text.equals(that.text) &&
            Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, text, photoUrl);
    }
}
