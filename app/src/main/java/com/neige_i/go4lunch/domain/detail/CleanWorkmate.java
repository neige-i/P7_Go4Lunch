package com.neige_i.go4lunch.domain.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class CleanWorkmate {

    @NonNull
    private final String email;
    @NonNull
    private final String name;
    @Nullable
    private final String photoUrl;
    private final boolean isCurrentUser;

    public CleanWorkmate(
        @NonNull String email,
        @NonNull String name,
        @Nullable String photoUrl,
        boolean isCurrentUser
    ) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.isCurrentUser = isCurrentUser;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CleanWorkmate that = (CleanWorkmate) o;
        return isCurrentUser == that.isCurrentUser &&
            email.equals(that.email) &&
            name.equals(that.name) &&
            Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, photoUrl, isCurrentUser);
    }

    @NonNull
    @Override
    public String toString() {
        return "CleanWorkmate{" +
            "email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", isCurrentUser=" + isCurrentUser +
            '}';
    }
}
