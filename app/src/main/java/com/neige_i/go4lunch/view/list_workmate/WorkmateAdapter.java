package com.neige_i.go4lunch.view.list_workmate;

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

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ItemWorkmateBinding;
import com.neige_i.go4lunch.view.ImageDelegate;

import java.util.Collections;

class WorkmateAdapter extends ListAdapter<WorkmateViewState, WorkmateAdapter.WorkmateViewHolder> {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final ImageDelegate imageDelegate;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final OnWorkmateClickedCallback onWorkmateClickedCallback;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    protected WorkmateAdapter(
        @NonNull ImageDelegate imageDelegate,
        @NonNull OnWorkmateClickedCallback onWorkmateClickedCallback
    ) {
        super(new WorkmateDiffCallback());
        this.imageDelegate = imageDelegate;
        this.onWorkmateClickedCallback = onWorkmateClickedCallback;
    }

    // ----------------------------------- LIST ADAPTER METHODS ------------------------------------

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmateViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmate, parent, false),
            onWorkmateClickedCallback
        );
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        final WorkmateViewState viewState = getItem(position);
        final Context context = holder.itemView.getContext();

        holder.itemView.setTag(viewState.getSelectedRestaurantId());
        holder.itemView.setEnabled(viewState.getSelectedRestaurantId() != null);

        imageDelegate.displayPhotoWithGlide(
            holder.binding.profileImg,
            viewState.getProfileImageUrl(),
            R.drawable.ic_person,
            Collections.singletonList(new CircleCrop())
        );

        holder.binding.workmateRestaurantLbl.setText(viewState.getText());
        holder.binding.workmateRestaurantLbl.setTypeface(null, viewState.getTextStyle());
        holder.binding.workmateRestaurantLbl.setTextColor(ContextCompat.getColor(context, viewState.getTextColor()));
    }

    // ------------------------------------- VIEW HOLDER CLASS -------------------------------------

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemWorkmateBinding binding;

        WorkmateViewHolder(
            @NonNull View itemView,
            @NonNull OnWorkmateClickedCallback onWorkmateClickedCallback
        ) {
            super(itemView);

            binding = ItemWorkmateBinding.bind(itemView);

            itemView.setOnClickListener(
                v -> onWorkmateClickedCallback.onWorkmateClicked(itemView.getTag().toString())
            );
        }
    }

    // ------------------------------------ DIFF UTIL CALLBACK -------------------------------------

    static class WorkmateDiffCallback extends DiffUtil.ItemCallback<WorkmateViewState> {

        @Override
        public boolean areItemsTheSame(
            @NonNull WorkmateViewState oldItem,
            @NonNull WorkmateViewState newItem
        ) {
            return oldItem.getWorkmateId().equals(newItem.getWorkmateId());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull WorkmateViewState oldItem,
            @NonNull WorkmateViewState newItem
        ) {
            return oldItem.equals(newItem);
        }
    }

    // -------------------------------------- CUSTOM CALLBACK --------------------------------------

    interface OnWorkmateClickedCallback {
        void onWorkmateClicked(@NonNull String placeId);
    }
}
