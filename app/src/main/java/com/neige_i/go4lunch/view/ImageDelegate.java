package com.neige_i.go4lunch.view;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ImageDelegate {

    void setStarVisibility(int rating, @NonNull ImageView... ratingStars);

    void displayPhotoWithGlide(@NonNull ImageView photoImg, @Nullable String photoUrl);
}
