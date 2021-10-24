package com.neige_i.go4lunch.view;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Transformation;

import java.util.List;

public interface ImageDelegate {

    void setStarVisibility(int rating, @NonNull ImageView... ratingStars);

    void displayPhotoWithGlide(
        @NonNull ImageView photoImg,
        @Nullable String photoUrl,
        @DrawableRes int errorDrawable,
        @NonNull List<Transformation<Bitmap>> transformations
    );
}
