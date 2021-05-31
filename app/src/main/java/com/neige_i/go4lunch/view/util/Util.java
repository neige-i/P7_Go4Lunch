package com.neige_i.go4lunch.view.util;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.PlacesResponse;

import java.util.List;
import java.util.Locale;

public abstract class Util {

    @NonNull
    public static String getPhotoUrl(@Nullable List<? extends PlacesResponse.Photo> photoList) {
        final String photoReference;
        if (photoList != null && !photoList.isEmpty())
            photoReference = photoList.get(0).getPhotoReference();
        else
            photoReference = "";

        return "https://maps.googleapis.com/" +
            "maps/api/place/photo?" +
            "maxheight=1080" +
            "&key=" + BuildConfig.MAPS_API_KEY +
            "&photoreference=" + photoReference;
    }

    /**
     * Converts Google rating (from 1.0 to 5.0) into Go4Lunch rating (from 0 to 3).<br />
     * [1.0,5.0] -> (-1) -> [0.0,4.0] -> (*.75) -> [0.0,3.0] -> (round) -> [0,3]
     */
    public static int getRating(@Nullable Double googleRating) {
        return (int) Math.round((googleRating - 1) * .75);
    }

    /**
     * Shortens the address and keeps only the street name and number.<br />
     * "2 Rue du Vivienne, 75005 Paris, France" -> "2 Rue du Vivienne"
     */
    @NonNull
    public static String getShortAddress(@Nullable String originalAddress) {
        return originalAddress.substring(0, originalAddress.indexOf(','));
    }

    public static void setPhotoSrcWithGlide(@NonNull ImageView photoImg, @NonNull String photoUrl) {
        final Transformation<Bitmap> centerCropTransformation = new CenterCrop();
        final Transformation<Bitmap> finalTransformation = photoImg.getId() == R.id.photo_img
            ? centerCropTransformation
            : new MultiTransformation<>(centerCropTransformation, new RoundedCorners(20));

        Glide
            .with(photoImg.getContext())
            .load(photoUrl)
            .transform(finalTransformation)
            .into(photoImg);
    }

    public static void setRatingImgVisibility(int rating, ImageView... ratingStars) {
        for (int i = 0; i < ratingStars.length; i++) {
            ratingStars[i].setVisibility(rating > i ? View.VISIBLE : View.GONE);
        }
    }

    public static String getFormattedDistance(float originalDistance) {
        return originalDistance < 1000
            ? String.format(Locale.getDefault(), "%.0fm", originalDistance)
            : String.format(Locale.getDefault(), "%.2fkm", originalDistance / 1000);
    }
}
