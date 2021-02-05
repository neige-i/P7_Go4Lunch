package com.neige_i.go4lunch.view.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.neige_i.go4lunch.R;

public class RestaurantAdapter extends ListAdapter<RestaurantViewState, RestaurantAdapter.RestaurantViewHolder> {

    @NonNull
    private final OnRestaurantClickedCallback onRestaurantClickedCallback;

    protected RestaurantAdapter(@NonNull OnRestaurantClickedCallback onRestaurantClickedCallback) {
        super(new RestaurantDiffCallback());
        this.onRestaurantClickedCallback = onRestaurantClickedCallback;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_restaurant,
            parent,
            false
        ), onRestaurantClickedCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        final RestaurantViewState viewState = getItem(position);
        final Context context = holder.itemView.getContext();

        holder.itemView.setTag(viewState.getPlaceId());
        holder.name.setText(viewState.getName());
        holder.distance.setText(String.format(context.getString(R.string.distance), viewState.getDistance()));
        holder.address.setText(viewState.getAddress());
        holder.openingHours.setText(viewState.getOpeningHours());
        holder.openingHours.setTypeface(null, viewState.getTextStyle());
        holder.openingHours.setTextColor(ContextCompat.getColor(context, viewState.getTextColor()));
        holder.workmatesLbl.setText(String.format(context.getString(R.string.workmate_count), viewState.getInterestedWorkmatesCount()));
        holder.workmatesLbl.setVisibility(viewState.areWorkmatesInterested() ? View.VISIBLE : View.GONE);
        holder.workmatesImg.setVisibility(viewState.areWorkmatesInterested() ? View.VISIBLE : View.GONE);
        holder.rating1.setVisibility(viewState.getRating() > 0 ? View.VISIBLE : View.GONE);
        holder.rating2.setVisibility(viewState.getRating() > 1 ? View.VISIBLE : View.GONE);
        holder.rating3.setVisibility(viewState.getRating() > 2 ? View.VISIBLE : View.GONE);
        Glide.with(context).load(viewState.getPhotoUrl()).into(holder.photo); // ASKME: handle cache
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView distance;
        private final TextView address;
        private final TextView openingHours;
        private final TextView workmatesLbl;
        private final ImageView workmatesImg;
        private final ImageView rating1;
        private final ImageView rating2;
        private final ImageView rating3;
        private final ImageView photo;

        public RestaurantViewHolder(@NonNull View itemView, @NonNull OnRestaurantClickedCallback onRestaurantClickedCallback) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            distance = itemView.findViewById(R.id.distance);
            address = itemView.findViewById(R.id.type_address);
            openingHours = itemView.findViewById(R.id.opening_hours);
            workmatesLbl = itemView.findViewById(R.id.person_count);
            workmatesImg = itemView.findViewById(R.id.person_icon);
            rating1 = itemView.findViewById(R.id.star1);
            rating2 = itemView.findViewById(R.id.star2);
            rating3 = itemView.findViewById(R.id.star3);
            photo = itemView.findViewById(R.id.profile_img);

            itemView.setOnClickListener(v -> onRestaurantClickedCallback.onRestaurantClicked(itemView.getTag().toString()));
        }
    }

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

    interface OnRestaurantClickedCallback {
        void onRestaurantClicked(String placeId);
    }
}
