package com.neige_i.go4lunch.view.chat;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Init view binding
        final ActivityChatBinding binding = ActivityChatBinding.inflate(getLayoutInflater());

        // Setup UI
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MessageAdapter messageAdapter = new MessageAdapter();
        binding.messageList.setAdapter(messageAdapter);

        binding.sendImage.setOnClickListener(v -> {
            viewModel.onSendImageClick(binding.messageInput.getText().toString());
        });

        // Update UI when state is changed
        viewModel.getViewState(getIntent().getStringExtra(EXTRA_USER_ID)).observe(this, chatViewState -> {
            binding.toolbar.setTitle(chatViewState.getWorkmateName());
            messageAdapter.submitList(chatViewState.getMessageViewStates());
        });

        // Setup actions when events are triggered
        viewModel.getGoBackEvent().observe(this, unused -> {
            finish();
        });
    }

    // ------------------------------------ OPTIONS MENU METHODS -----------------------------------

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        viewModel.onMenuItemClick(item.getItemId());
        return false;
    }
}