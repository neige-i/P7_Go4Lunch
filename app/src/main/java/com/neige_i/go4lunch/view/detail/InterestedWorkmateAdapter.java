package com.neige_i.go4lunch.view.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ItemWorkmateBinding;
import com.neige_i.go4lunch.view.ImageDelegate;

import java.util.Collections;

class InterestedWorkmateAdapter extends ListAdapter<WorkmateViewState, InterestedWorkmateAdapter.WorkmateViewHolder> {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final ImageDelegate imageDelegate;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    protected InterestedWorkmateAdapter(@NonNull ImageDelegate imageDelegate) {
        super(new WorkmateDiffCallback());
        this.imageDelegate = imageDelegate;
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
        final WorkmateViewState viewState = getItem(position);

        holder.binding.workmateRestaurantLbl.setText(viewState.getText());

        imageDelegate.displayPhotoWithGlide(
            holder.binding.profileImg,
            viewState.getPhotoUrl(),
            R.drawable.ic_person,
            Collections.singletonList(new CircleCrop())
        );

        holder.binding.chatImage.setVisibility(View.GONE);
        holder.binding.horizontalLine.setVisibility(View.GONE);
    }

    // ------------------------------------- VIEW HOLDER CLASS -------------------------------------

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemWorkmateBinding binding;

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemWorkmateBinding.bind(itemView);
        }
    }

    // ------------------------------------ DIFF UTIL CALLBACK -------------------------------------

    static class WorkmateDiffCallback extends DiffUtil.ItemCallback<WorkmateViewState> {

        @Override
        public boolean areItemsTheSame(
            @NonNull WorkmateViewState oldItem,
            @NonNull WorkmateViewState newItem
        ) {
            return oldItem.getEmail().equals(newItem.getEmail());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull WorkmateViewState oldItem,
            @NonNull WorkmateViewState newItem
        ) {
            return oldItem.equals(newItem);
        }
    }
}
