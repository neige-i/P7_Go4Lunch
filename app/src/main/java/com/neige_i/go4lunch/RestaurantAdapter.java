package com.neige_i.go4lunch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantAdapter extends ListAdapter<Object, RestaurantAdapter.RestaurantViewHolder> {

    protected RestaurantAdapter() {
        super(new RestaurantDiffCallback());
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_restaurant,
            parent,
            false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {

    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class RestaurantDiffCallback extends DiffUtil.ItemCallback<Object> {

        @Override
        public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            return false;
        }
    }
}
