package com.neige_i.go4lunch.view.chat;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ItemChatBinding;

class MessageAdapter extends ListAdapter<ChatViewState.MessageViewState, MessageAdapter.MessageViewHolder> {

    @NonNull
    private final OnGetItemCountCallback onGetItemCountCallback;

    MessageAdapter(@NonNull OnGetItemCountCallback onGetItemCountCallback) {
        super(new MessageDiffUtil());
        this.onGetItemCountCallback = onGetItemCountCallback;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        final int itemCount = super.getItemCount();
        onGetItemCountCallback.onGetItemCount(itemCount);
        return itemCount;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatBinding binding;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemChatBinding.bind(itemView);
        }

        void bind(@NonNull ChatViewState.MessageViewState viewState) {
            final ConstraintLayout.LayoutParams dateTimeLayoutParams =
                (ConstraintLayout.LayoutParams) binding.dateTimeText.getLayoutParams();
            final ConstraintLayout.LayoutParams messageLayoutParams =
                (ConstraintLayout.LayoutParams) binding.messageText.getLayoutParams();

            dateTimeLayoutParams.horizontalBias = viewState.getHorizontalBias();
            messageLayoutParams.horizontalBias = viewState.getHorizontalBias();
            messageLayoutParams.setMarginStart(getPixelSize(viewState.getMarginStart()));
            messageLayoutParams.setMarginEnd(getPixelSize(viewState.getMarginEnd()));

            binding.dateTimeText.setLayoutParams(dateTimeLayoutParams);
            binding.messageText.setLayoutParams(messageLayoutParams);

            ViewCompat.setBackgroundTintList(
                binding.messageText,
                ContextCompat.getColorStateList(itemView.getContext(), viewState.getBackgroundColor())
            );

            binding.messageText.setText(viewState.getMessage());
            binding.dateTimeText.setText(viewState.getDateTime());
        }

        private int getPixelSize(int dpSize) {
            return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpSize,
                itemView.getResources().getDisplayMetrics()
            );
        }
    }

    static class MessageDiffUtil extends DiffUtil.ItemCallback<ChatViewState.MessageViewState> {

        @Override
        public boolean areItemsTheSame(
            @NonNull ChatViewState.MessageViewState oldItem,
            @NonNull ChatViewState.MessageViewState newItem
        ) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull ChatViewState.MessageViewState oldItem,
            @NonNull ChatViewState.MessageViewState newItem
        ) {
            return oldItem.equals(newItem);
        }
    }

    interface OnGetItemCountCallback {

        void onGetItemCount(int itemCount);
    }
}
