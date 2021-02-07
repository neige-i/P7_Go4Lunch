package com.neige_i.go4lunch.view.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.go4lunch.R;

class WorkmateAdapter extends ListAdapter<Object, WorkmateAdapter.WorkmateViewHolder> {

    protected WorkmateAdapter() {
        super(new WorkmateDiffCallback());
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmateViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_workmate,
            parent,
            false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {

    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class WorkmateDiffCallback extends DiffUtil.ItemCallback<Object> {

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
