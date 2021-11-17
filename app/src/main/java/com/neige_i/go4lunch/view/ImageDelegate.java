package com.neige_i.go4lunch.view;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.neige_i.go4lunch.R;

import java.util.List;

import javax.inject.Inject;

public class ImageDelegate {

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public ImageDelegate() {
    }

    // ------------------------------------- DELEGATE METHODS --------------------------------------

    public void setStarVisibility(int rating, @NonNull ImageView... ratingStars) {
        for (int i = 0; i < ratingStars.length; i++) {
            ratingStars[i].setVisibility(rating > i ? View.VISIBLE : View.GONE);
        }
    }

    public void displayPhotoWithGlide(
        @NonNull ImageView photoImg,
        @Nullable String photoUrl,
        @DrawableRes int errorDrawable,
        @NonNull List<Transformation<Bitmap>> transformations
    ) {
        Glide.with(photoImg.getContext())
            .load(photoUrl)
            .placeholder(R.color.gray_light)
            .error(errorDrawable)
            .transform(new MultiTransformation<>(transformations))
            .into(photoImg);
    }
}
