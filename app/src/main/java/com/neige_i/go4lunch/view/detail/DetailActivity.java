package com.neige_i.go4lunch.view.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.util.Util;
import com.neige_i.go4lunch.view.util.ViewModelFactory;

import static com.neige_i.go4lunch.view.home.HomeActivity.EXTRA_PLACE_ID;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final DetailViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(DetailViewModel.class);
        viewModel.onInfoQueried(getIntent().getStringExtra(EXTRA_PLACE_ID));

        final ImageView photoImg = findViewById(R.id.photo_img);
        final ImageView rating1 = findViewById(R.id.star1_ic);
        final ImageView rating2 = findViewById(R.id.star2_ic);
        final ImageView rating3 = findViewById(R.id.star3_ic);
        final TextView nameLbl = findViewById(R.id.name_lbl);
        final TextView addressLbl = findViewById(R.id.address_lbl);
        final Button callBtn = findViewById(R.id.call_btn);
        final Button likeBtn = findViewById(R.id.like_btn);
        final Button websiteBtn = findViewById(R.id.website_btn);
        final CheckBox selected = findViewById(R.id.selected_checkbox);

        // ASKME: mandatory null check of Intent.resolveActivity(getPackageManager())
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
            Util.setPhotoSrcWithGlide(photoImg, detailViewState.getPhotoUrl());
            Util.setRatingImgVisibility(detailViewState.getRating(), rating1, rating2, rating3);
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
        });
    }
}