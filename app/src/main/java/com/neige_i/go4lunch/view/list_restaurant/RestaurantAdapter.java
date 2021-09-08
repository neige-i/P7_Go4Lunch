package com.neige_i.go4lunch.view.list_restaurant;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ItemRestaurantBinding;

class RestaurantAdapter extends ListAdapter<RestaurantViewState, RestaurantAdapter.RestaurantViewHolder> {

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final OnRestaurantClickedCallback onRestaurantClickedCallback;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    protected RestaurantAdapter(@NonNull OnRestaurantClickedCallback onRestaurantClickedCallback) {
        super(new RestaurantDiffCallback());
        this.onRestaurantClickedCallback = onRestaurantClickedCallback;
    }

    // ----------------------------------- LIST ADAPTER METHODS ------------------------------------

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false),
            onRestaurantClickedCallback
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        final RestaurantViewState viewState = getItem(position);
        final Context context = holder.itemView.getContext();

        holder.itemView.setTag(viewState.getPlaceId());
        holder.binding.nameLbl.setText(viewState.getName());
        holder.binding.distanceLbl.setText(viewState.getFormattedDistance());
        holder.binding.addressLbl.setText(viewState.getAddress());
        holder.binding.openingHoursLbl.setText(viewState.getOpeningHours());
        holder.binding.openingHoursLbl.setTypeface(null, viewState.getTextStyle());
        holder.binding.openingHoursLbl.setTextColor(ContextCompat.getColor(context, viewState.getTextColor()));
        holder.binding.coworkerCountLbl.setText(String.format(context.getString(R.string.workmate_count), viewState.getInterestedWorkmatesCount()));
        holder.binding.coworkerCountLbl.setVisibility(viewState.areWorkmatesInterested() ? View.VISIBLE : View.GONE);
        holder.binding.coworkerImg.setVisibility(viewState.areWorkmatesInterested() ? View.VISIBLE : View.GONE);
        setRatingImgVisibility(viewState.getRating(), holder.binding.star1Img, holder.binding.star2Img, holder.binding.star3Img);
        holder.binding.noRatingLbl.setVisibility(viewState.isNoRatingLblVisible() ? View.VISIBLE : View.GONE);
        setPhotoSrcWithGlide(holder.binding.thumbnailImg, viewState.getPhotoUrl());
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

    // ------------------------------------- VIEW HOLDER CLASS -------------------------------------

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemRestaurantBinding binding;

        RestaurantViewHolder(@NonNull View itemView, @NonNull OnRestaurantClickedCallback onRestaurantClickedCallback) {
            super(itemView);

            binding = ItemRestaurantBinding.bind(itemView);

            itemView.setOnClickListener(
                v -> onRestaurantClickedCallback.onRestaurantClicked(itemView.getTag().toString())
            );
        }
    }

    // ------------------------------------ DIFF UTIL CALLBACK -------------------------------------

    static class RestaurantDiffCallback extends DiffUtil.ItemCallback<RestaurantViewState> {

        @Override
        public boolean areItemsTheSame(@NonNull RestaurantViewState oldItem, @NonNull RestaurantViewState newItem) {
            return oldItem.getPlaceId().equals(newItem.getPlaceId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RestaurantViewState oldItem, @NonNull RestaurantViewState newItem) {
            return oldItem.equals(newItem);
        }
    }

    // -------------------------------------- CUSTOM CALLBACK --------------------------------------

    interface OnRestaurantClickedCallback {
        void onRestaurantClicked(@NonNull String placeId);
    }
}
