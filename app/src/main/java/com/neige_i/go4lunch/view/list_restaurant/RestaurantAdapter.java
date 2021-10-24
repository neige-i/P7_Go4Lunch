package com.neige_i.go4lunch.view.list_restaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ItemRestaurantBinding;
import com.neige_i.go4lunch.view.ImageDelegate;

import java.util.Arrays;

class RestaurantAdapter extends ListAdapter<RestaurantViewState, RestaurantAdapter.RestaurantViewHolder> {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final ImageDelegate imageDelegate;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final OnRestaurantClickedCallback onRestaurantClickedCallback;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    protected RestaurantAdapter(
        @NonNull ImageDelegate imageDelegate,
        @NonNull OnRestaurantClickedCallback onRestaurantClickedCallback
    ) {
        super(new RestaurantDiffCallback());
        this.imageDelegate = imageDelegate;
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

    @SuppressLint("WrongConstant")
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

        final int interestedWorkmateCount = viewState.getInterestedWorkmatesCount();
        holder.binding.coworkerCountLbl.setText(String.format(context.getString(R.string.workmate_count), interestedWorkmateCount));
        holder.binding.coworkerCountLbl.setVisibility(interestedWorkmateCount > 0 ? View.VISIBLE : View.GONE);
        holder.binding.coworkerImg.setVisibility(interestedWorkmateCount > 0 ? View.VISIBLE : View.GONE);

        holder.binding.noRatingLbl.setVisibility(viewState.getRating() == -1 ? View.VISIBLE : View.GONE);
        imageDelegate.setStarVisibility(
            viewState.getRating(),
            holder.binding.star1Img,
            holder.binding.star2Img,
            holder.binding.star3Img
        );

        imageDelegate.displayPhotoWithGlide(
            holder.binding.thumbnailImg,
            viewState.getPhotoUrl(),
            R.drawable.ic_no_image,
            Arrays.asList(new CenterCrop(), new RoundedCorners(20))
        );
    }

    // ------------------------------------- VIEW HOLDER CLASS -------------------------------------

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemRestaurantBinding binding;

        RestaurantViewHolder(
            @NonNull View itemView,
            @NonNull OnRestaurantClickedCallback onRestaurantClickedCallback
        ) {
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
        public boolean areItemsTheSame(
            @NonNull RestaurantViewState oldItem,
            @NonNull RestaurantViewState newItem
        ) {
            return oldItem.getPlaceId().equals(newItem.getPlaceId());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull RestaurantViewState oldItem,
            @NonNull RestaurantViewState newItem
        ) {
            return oldItem.equals(newItem);
        }
    }

    // -------------------------------------- CUSTOM CALLBACK --------------------------------------

    interface OnRestaurantClickedCallback {
        void onRestaurantClicked(@NonNull String placeId);
    }
}
