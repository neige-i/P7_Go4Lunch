package com.neige_i.go4lunch.view.detail;

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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.neige_i.go4lunch.R;

class InterestedWorkmateAdapter extends ListAdapter<String, InterestedWorkmateAdapter.WorkmateViewHolder> {

    protected InterestedWorkmateAdapter() {
        super(new WorkmateDiffCallback());
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmateViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmate, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
//        final String viewState = getItem(position);

//        holder.itemView.setTag(viewState.getSelectedRestaurantId());

//        Glide
//            .with(holder.profileImg.getContext())
//            .load(viewState.getProfileImageUrl())
//            .transform(new CircleCrop())
//            .into(holder.profileImg);
        holder.workmateAndRestaurantTxt.setText(getItem(position));
//        holder.workmateAndRestaurantTxt.setText(viewState.getNameAndSelectedRestaurant());
//        holder.workmateAndRestaurantTxt.setTypeface(null, viewState.getTextStyle());
//        holder.workmateAndRestaurantTxt.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), viewState.getTextColor()));
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

//        private final ImageView profileImg;
        private final TextView workmateAndRestaurantTxt;

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

//            profileImg = itemView.findViewById(R.id.profile_img);
            workmateAndRestaurantTxt = itemView.findViewById(R.id.workmate_restaurant_lbl);
        }
    }

    static class WorkmateDiffCallback extends DiffUtil.ItemCallback<String> {

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    }
}
