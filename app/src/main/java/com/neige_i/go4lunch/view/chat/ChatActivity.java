package com.neige_i.go4lunch.view.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.databinding.ActivityChatBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends AppCompatActivity {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    public static final String EXTRA_USER_ID = BuildConfig.APPLICATION_ID + ".userId";

    // ---------------------------------------- LOCAL FIELDS ---------------------------------------

    private ChatViewModel viewModel;

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String workmateId = getIntent().getStringExtra(EXTRA_USER_ID);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.onActivityCreated(workmateId);

        // Init view binding
        final ActivityChatBinding binding = ActivityChatBinding.inflate(getLayoutInflater());

        // Setup UI
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MessageAdapter messageAdapter = new MessageAdapter(itemCount -> {
            viewModel.onMessageListItemCountCalled(itemCount);
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.messageList.setAdapter(messageAdapter);
        binding.messageList.setLayoutManager(linearLayoutManager);
        binding.messageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                viewModel.onMessageListScrolled(linearLayoutManager.findLastVisibleItemPosition());
            }
        });

        binding.messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onMessageChanged(s.toString());
            }
        });

        binding.sendMessageButton.setOnClickListener(v -> {
            viewModel.onSendButtonClick(workmateId, binding.messageInput.getText().toString());
        });

        binding.scrollBottomButton.setOnClickListener(v -> {
            viewModel.onScrollBottomButtonClicked();
        });

        // Update UI when state is changed
        viewModel.getViewState().observe(this, chatViewState -> {
            binding.toolbar.setTitle(chatViewState.getWorkmateName());

            messageAdapter.submitList(chatViewState.getMessageViewStates());
            binding.noMessagesText.setVisibility(chatViewState.isTextViewVisible() ? View.VISIBLE : View.GONE);

            binding.sendMessageButton.setEnabled(chatViewState.isFabEnabled());
            binding.sendMessageButton.setAlpha(chatViewState.getFabAlpha());

            binding.scrollBottomButton.setVisibility(chatViewState.isScrollBottomButtonVisible() ? View.VISIBLE : View.GONE);
        });

        // Setup actions when events are triggered
        viewModel.getScrollToPositionEvent().observe(this, scrollPosition -> {
            binding.messageList.scrollToPosition(scrollPosition);
        });
        viewModel.getClearInputEvent().observe(this, unused -> binding.messageInput.setText(null));
        viewModel.getGoBackEvent().observe(this, unused -> finish());
    }

    // ------------------------------------ OPTIONS MENU METHODS -----------------------------------

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        viewModel.onMenuItemClick(item.getItemId());
        return false;
    }
}