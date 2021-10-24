package com.neige_i.go4lunch.view;

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
import com.neige_i.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ImageDelegateImpl implements ImageDelegate {

    @Inject
    public ImageDelegateImpl() {
    }

    @Override
    public void setStarVisibility(int rating, @NonNull ImageView... ratingStars) {
        for (int i = 0; i < ratingStars.length; i++) {
            ratingStars[i].setVisibility(rating > i ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void displayPhotoWithGlide(@NonNull ImageView photoImg, @Nullable String photoUrl) {
        final List<Transformation<Bitmap>> transformationList = new ArrayList<>();
        transformationList.add(new CenterCrop());
        if (photoImg.getId() != R.id.photo_img) {
            transformationList.add(new RoundedCorners(20));
        }

        Glide.with(photoImg.getContext())
            .load(photoUrl)
            .placeholder(R.color.gray_light)
            .error(R.drawable.ic_no_image)
            .transform(new MultiTransformation<>(transformationList))
            .into(photoImg);
    }
}
