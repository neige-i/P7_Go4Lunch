package com.neige_i.go4lunch.view.chat;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.ItemChatBinding;

class MessageAdapter extends ListAdapter<ChatViewState.MessageViewState, MessageAdapter.MessageViewHolder> {

    MessageAdapter() {
        super(new MessageDiffUtil());
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

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatBinding binding;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemChatBinding.bind(itemView);
        }

        void bind(@NonNull ChatViewState.MessageViewState viewState) {
            final ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) binding.card.getLayoutParams();

            layoutParams.horizontalBias = viewState.getHorizontalBias();
            layoutParams.setMarginStart(getPixelSize(viewState.getMarginStart()));
            layoutParams.setMarginEnd(getPixelSize(viewState.getMarginEnd()));

            binding.card.setLayoutParams(layoutParams);
            binding.card.setCardBackgroundColor(
                ContextCompat.getColor(itemView.getContext(), viewState.getBackgroundColor())
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
}
