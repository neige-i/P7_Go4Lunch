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
    private final OnWorkmateClickCallback onWorkmateClickCallback;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    protected WorkmateAdapter(
        @NonNull ImageDelegate imageDelegate,
        @NonNull OnWorkmateClickCallback onWorkmateClickCallback
    ) {
        super(new WorkmateDiffCallback());
        this.imageDelegate = imageDelegate;
        this.onWorkmateClickCallback = onWorkmateClickCallback;
    }

    // ----------------------------------- LIST ADAPTER METHODS ------------------------------------

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmateViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmate, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        holder.bind(getItem(position), imageDelegate, onWorkmateClickCallback);
    }

    // ------------------------------------- VIEW HOLDER CLASS -------------------------------------

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemWorkmateBinding binding;

        WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemWorkmateBinding.bind(itemView);
        }

        @SuppressLint("WrongConstant")
        void bind(
            @NonNull WorkmateViewState viewState,
            @NonNull ImageDelegate imageDelegate,
            @NonNull OnWorkmateClickCallback onWorkmateClickCallback
        ) {
            final Context context = itemView.getContext();

            imageDelegate.displayPhotoWithGlide(
                binding.profileImg,
                viewState.getProfileImageUrl(),
                R.drawable.ic_person,
                Collections.singletonList(new CircleCrop())
            );

            binding.workmateRestaurantLbl.setText(viewState.getText());
            binding.workmateRestaurantLbl.setTypeface(null, viewState.getTextStyle());
            binding.workmateRestaurantLbl.setTextColor(ContextCompat.getColor(context, viewState.getTextColor()));

            itemView.setEnabled(viewState.getSelectedRestaurantId() != null);
            itemView.setOnClickListener(v -> {
                onWorkmateClickCallback.onWorkmateClick(viewState.getSelectedRestaurantId());
            });

            binding.chatImage.setVisibility(viewState.isChatButtonVisible() ? View.VISIBLE : View.GONE);
            binding.chatImage.setOnClickListener(v -> {
                onWorkmateClickCallback.onChatButtonClick(viewState.getWorkmateId());
            });
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

    interface OnWorkmateClickCallback {

        void onWorkmateClick(@NonNull String placeId);

        void onChatButtonClick(@NonNull String userId);
    }
}
