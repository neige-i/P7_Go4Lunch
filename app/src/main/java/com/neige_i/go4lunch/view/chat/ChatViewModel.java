package com.neige_i.go4lunch.view.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.view.SingleLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class ChatViewModel extends ViewModel {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final SingleLiveEvent<Void> goBackEvent = new SingleLiveEvent<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public ChatViewModel() {
    }

    @NonNull
    public LiveData<Void> getGoBackEvent() {
        return goBackEvent;
    }

    // ------------------------------------ VIEW STATE METHODS -------------------------------------

    @NonNull
    LiveData<ChatViewState> getViewState(@NonNull String workmateId) {
        return new MutableLiveData<>();
    }

    // ---------------------------------------- UI METHODS -----------------------------------------

    void onSendImageClick(@NonNull String messageToSend) {
    }

    // ------------------------------------ NAVIGATION METHODS -------------------------------------

    void onMenuItemClick(int itemId) {
        if (itemId == android.R.id.home) {
            goBackEvent.call();
        }
    }
}
