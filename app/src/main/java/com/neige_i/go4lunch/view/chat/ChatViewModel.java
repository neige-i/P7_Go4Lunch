package com.neige_i.go4lunch.view.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.chat.AddMessageUseCase;
import com.neige_i.go4lunch.domain.chat.ChatInfo;
import com.neige_i.go4lunch.domain.chat.GetChatInfoUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class ChatViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final AddMessageUseCase addMessageUseCase;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MediatorLiveData<ChatViewState> viewState = new MediatorLiveData<>();
    @NonNull
    private final SingleLiveEvent<Void> goBackEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Void> clearInputEvent = new SingleLiveEvent<>();

    // ---------------------------------------- LOCAL FIELDS ---------------------------------------

    @NonNull
    private final MutableLiveData<String> workmateIdMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isMessageEmptyMutableLiveData = new MutableLiveData<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public ChatViewModel(
        @NonNull GetChatInfoUseCase getChatInfoUseCase,
        @NonNull AddMessageUseCase addMessageUseCase
    ) {
        this.addMessageUseCase = addMessageUseCase;

        final LiveData<ChatInfo> chatInfoLiveData = Transformations.switchMap(
            workmateIdMutableLiveData, workmateId -> getChatInfoUseCase.get(workmateId)
        );
        viewState.addSource(chatInfoLiveData, chatInfo -> combine(chatInfo, isMessageEmptyMutableLiveData.getValue()));
        viewState.addSource(isMessageEmptyMutableLiveData, isMessageEmpty -> combine(chatInfoLiveData.getValue(), isMessageEmpty));
    }

    private void combine(@Nullable ChatInfo chatInfo, @Nullable Boolean isMessageEmpty) {
        if (chatInfo == null || isMessageEmpty == null) {
            return;
        }

        final List<ChatViewState.MessageViewState> messageViewStates = chatInfo.getMessageInfoList()
            .stream()
            .map(messageInfo -> {
                return new ChatViewState.MessageViewState(
                    messageInfo.getText(),
                    messageInfo.getDateTime(),
                    messageInfo.isCurrentUserSender() ? R.color.orange_light : R.color.gray_light,
                    messageInfo.isCurrentUserSender() ? 1 : 0,
                    messageInfo.isCurrentUserSender() ? 100 : 0,
                    messageInfo.isCurrentUserSender() ? 0 : 100
                );
            })
            .collect(Collectors.toList());

        viewState.setValue(new ChatViewState(
            chatInfo.getWorkmateName(),
            messageViewStates,
            messageViewStates.isEmpty(),
            !isMessageEmpty,
            !isMessageEmpty ? 1f : .75f
        ));
    }

    @NonNull
    LiveData<ChatViewState> getViewState() {
        return viewState;
    }

    @NonNull
    public LiveData<Void> getGoBackEvent() {
        return goBackEvent;
    }

    @NonNull
    public LiveData<Void> getClearInputEvent() {
        return clearInputEvent;
    }

    // --------------------------------------- CHAT METHODS ----------------------------------------

    void onActivityCreated(@NonNull String workmateId) {
        workmateIdMutableLiveData.setValue(workmateId);
        isMessageEmptyMutableLiveData.setValue(true);
    }

    public void onMessageChanged(@NonNull String message) {
        isMessageEmptyMutableLiveData.setValue(isMessageEmpty(message));
    }

    void onSendButtonClick(@NonNull String workmateId, @NonNull String messageToSend) {
        if (!isMessageEmpty(messageToSend)) {
            addMessageUseCase.add(workmateId, messageToSend);
            clearInputEvent.call();
        }
    }

    // ------------------------------------ NAVIGATION METHODS -------------------------------------

    void onMenuItemClick(int itemId) {
        if (itemId == android.R.id.home) {
            goBackEvent.call();
        }
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private boolean isMessageEmpty(@NonNull String message) {
        return message.trim().isEmpty();
    }
}
