package com.neige_i.go4lunch.view.detail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.ViewModelFactory;

import static com.neige_i.go4lunch.view.home.HomeActivity.EXTRA_PLACE_ID;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final DetailViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(DetailViewModel.class);

        final TextView nameLbl = findViewById(R.id.name);
        final TextView addressLbl = findViewById(R.id.type_address);
        final Button callBtn = findViewById(R.id.call_btn);
        final Button likeBtn = findViewById(R.id.like_btn);
        final Button websiteBtn = findViewById(R.id.website_btn);
        final CheckBox eatHere = findViewById(R.id.custom_checkbox);

        viewModel.getViewState(getIntent().getStringExtra(EXTRA_PLACE_ID)).observe(this, detailViewState -> {
            nameLbl.setText(detailViewState.getName());
            addressLbl.setText(detailViewState.getAddress());
            callBtn.setTag(detailViewState.getPhoneNumber());
            websiteBtn.setTag(detailViewState.getWebsite());
            likeBtn.setTag(detailViewState.isFavorite());
            eatHere.setChecked(detailViewState.isSelected());
        });
    }
}