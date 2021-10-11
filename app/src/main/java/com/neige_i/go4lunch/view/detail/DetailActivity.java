package com.neige_i.go4lunch.view.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.neige_i.go4lunch.R;

import static com.neige_i.go4lunch.view.home.HomeActivity.EXTRA_PLACE_ID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final DetailViewModel viewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        viewModel.onInfoQueried(getIntent().getStringExtra(EXTRA_PLACE_ID));

        final ImageView photoImg = findViewById(R.id.photo_img);
        final ImageView rating1 = findViewById(R.id.star1_img);
        final ImageView rating2 = findViewById(R.id.star2_img);
        final ImageView rating3 = findViewById(R.id.star3_img);
        final TextView noRatingLbl = findViewById(R.id.no_rating_lbl);
        final TextView nameLbl = findViewById(R.id.name_lbl);
        final TextView addressLbl = findViewById(R.id.address_lbl);
        final Button callBtn = findViewById(R.id.call_btn);
        final Button likeBtn = findViewById(R.id.like_btn);
        final Button websiteBtn = findViewById(R.id.website_btn);
        final CheckBox selected = findViewById(R.id.selected_checkbox);
        final InterestedWorkmateAdapter adapter = new InterestedWorkmateAdapter();
        ((RecyclerView) findViewById(R.id.interested_workmate_list)).setAdapter(adapter);

        // TODO: add null check of Intent.resolveActivity(getPackageManager())
        callBtn.setOnClickListener(v -> startActivity(new Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:" + callBtn.getTag())
        )));
        websiteBtn.setOnClickListener(v -> startActivity(new Intent(
            Intent.ACTION_VIEW,
            Uri.parse((String) websiteBtn.getTag())
        )));
        likeBtn.setOnClickListener(v -> viewModel.onLikeBtnClicked());
        selected.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.onSelectedRestaurantUpdated(isChecked));

        viewModel.getViewState().observe(this, detailViewState -> {
            setPhotoSrcWithGlide(photoImg, detailViewState.getPhotoUrl());
            setRatingImgVisibility(detailViewState.getRating(), rating1, rating2, rating3);
            noRatingLbl.setVisibility(detailViewState.isNoRatingLblVisible() ? View.VISIBLE : View.GONE);
            nameLbl.setText(detailViewState.getName());
            addressLbl.setText(detailViewState.getAddress());
            callBtn.setTag(detailViewState.getPhoneNumber());
            websiteBtn.setTag(detailViewState.getWebsite());
            likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                0,
                (detailViewState.isFavorite()) ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off,
                0,
                0
            );
            selected.setChecked(detailViewState.isSelected());
            adapter.submitList(detailViewState.getWorkmateIds());
        });
    }

    // TODO: remove
    private void setRatingImgVisibility(int rating, ImageView... ratingStars) {
        for (int i = 0; i < ratingStars.length; i++) {
            ratingStars[i].setVisibility(rating > i ? View.VISIBLE : View.GONE);
        }
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
}