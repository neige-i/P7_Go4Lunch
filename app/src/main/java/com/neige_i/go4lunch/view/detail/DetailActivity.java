package com.neige_i.go4lunch.view.detail;

import static com.neige_i.go4lunch.view.home.HomeActivity.EXTRA_PLACE_ID;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ActivityDetailBinding;
import com.neige_i.go4lunch.view.ImageDelegate;

import java.util.Collections;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailActivity extends AppCompatActivity {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @Inject
    ImageDelegate imageDelegate;

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);

        // Init ViewModel
        final DetailViewModel viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        // Init view binding
        final ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());

        // Setup UI
        setContentView(binding.getRoot());

        final InterestedWorkmateAdapter adapter = new InterestedWorkmateAdapter(imageDelegate);
        binding.interestedWorkmateList.setAdapter(adapter);

        binding.callBtn.setOnClickListener(v -> {
            final String action = Intent.ACTION_DIAL;
            final String uriString = "tel:" + binding.callBtn.getTag();
            viewModel.onExternalActivityAsked(isActivityResolved(action, uriString), action, uriString);
        });
        binding.websiteBtn.setOnClickListener(v -> {
            final String action = Intent.ACTION_VIEW;
            final String uriString = (String) binding.websiteBtn.getTag();
            viewModel.onExternalActivityAsked(isActivityResolved(action, uriString), action, uriString);
        });
        binding.likeBtn.setOnClickListener(v -> viewModel.onLikeButtonClicked(placeId));
        binding.checkBtn.setOnClickListener(v -> viewModel.onSelectedRestaurantClicked(placeId));

        // Update UI when state is changed
        viewModel.getViewState(placeId).observe(this, detailViewState -> {
            imageDelegate.displayPhotoWithGlide(
                binding.photoImg,
                detailViewState.getPhotoUrl(),
                R.drawable.ic_no_image,
                Collections.singletonList(new CenterCrop())
            );

            imageDelegate.setStarVisibility(
                detailViewState.getRating(),
                binding.star1Img,
                binding.star2Img,
                binding.star3Img
            );
            binding.noRatingLbl.setVisibility(detailViewState.getRating() == -1 ? View.VISIBLE : View.GONE);

            binding.nameLbl.setText(detailViewState.getName());
            binding.addressLbl.setText(detailViewState.getAddress());

            binding.callBtn.setTag(detailViewState.getPhoneNumber());
            binding.callBtn.setEnabled(detailViewState.getPhoneNumber() != null);

            binding.websiteBtn.setTag(detailViewState.getWebsite());
            binding.websiteBtn.setEnabled(detailViewState.getWebsite() != null);

            binding.likeBtn.setCompoundDrawablesWithIntrinsicBounds(
                0,
                (detailViewState.isFavorite()) ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off,
                0,
                0
            );

            binding.checkBtn.setImageResource(detailViewState.getCheckButtonDrawable());
            binding.checkBtn.setColorFilter(ContextCompat.getColor(this, detailViewState.getCheckButtonColor()));

            adapter.submitList(detailViewState.getWorkmateViewStates());
        });

        // Update UI when events are triggered
        viewModel.getStartExternalActivityEvent().observe(this, intentInfo -> {
            startActivity(getExternalIntent(intentInfo[0], intentInfo[1]));
        });
    }

    private boolean isActivityResolved(@NonNull String action, @NonNull String uriString) {
        return getExternalIntent(action, uriString).resolveActivity(getPackageManager()) != null;
    }

    @NonNull
    private Intent getExternalIntent(@NonNull String action, @NonNull String uriString) {
        return new Intent(action, Uri.parse(uriString));
    }
}