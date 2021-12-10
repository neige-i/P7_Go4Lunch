package com.neige_i.go4lunch.view.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.databinding.ItemAutocompleteBinding;

class AutocompleteAdapter extends ListAdapter<AutocompleteRestaurant, AutocompleteAdapter.AutocompleteViewHolder> {

    @NonNull
    private final OnAutocompleteResultClickCallback onAutocompleteResultClickCallback;

    AutocompleteAdapter(@NonNull OnAutocompleteResultClickCallback onAutocompleteResultClickCallback) {
        super(new AutocompleteDiffUtil());
        this.onAutocompleteResultClickCallback = onAutocompleteResultClickCallback;
    }

    @NonNull
    @Override
    public AutocompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AutocompleteViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_autocomplete,
            parent,
            false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AutocompleteViewHolder holder, int position) {
        holder.bind(getItem(position), onAutocompleteResultClickCallback);
    }

    static class AutocompleteViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final ItemAutocompleteBinding binding;

        public AutocompleteViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemAutocompleteBinding.bind(itemView);
        }

        void bind(
            @NonNull AutocompleteRestaurant autocompleteRestaurant,
            @NonNull OnAutocompleteResultClickCallback onAutocompleteResultClickCallback
        ) {
            binding.searchResultLabel.setText(autocompleteRestaurant.getRestaurantName());

            itemView.setOnClickListener(v -> {
                onAutocompleteResultClickCallback.onClick(autocompleteRestaurant);
            });
        }
    }

    static class AutocompleteDiffUtil extends DiffUtil.ItemCallback<AutocompleteRestaurant> {

        @Override
        public boolean areItemsTheSame(
            @NonNull AutocompleteRestaurant oldItem,
            @NonNull AutocompleteRestaurant newItem
        ) {
            return oldItem.getPlaceId().equals(newItem.getPlaceId());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull AutocompleteRestaurant oldItem,
            @NonNull AutocompleteRestaurant newItem
        ) {
            return oldItem.equals(newItem);
        }
    }

    interface OnAutocompleteResultClickCallback {
        void onClick(@NonNull AutocompleteRestaurant restaurantName);
    }
}
