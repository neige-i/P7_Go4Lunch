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
import java.util.Objects;
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
    private final SingleLiveEvent<Integer> scrollToPositionEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Void> goBackEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Void> clearInputEvent = new SingleLiveEvent<>();

    // ---------------------------------------- LOCAL FIELDS ---------------------------------------

    @NonNull
    private final MutableLiveData<String> workmateIdMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isMessageEmptyMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> scrollBottomButtonVisibilityMutableLiveData = new MutableLiveData<>();
    private int currentItemCount;
    private boolean isScrollBottomButtonVisible;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public ChatViewModel(
        @NonNull GetChatInfoUseCase getChatInfoUseCase,
        @NonNull AddMessageUseCase addMessageUseCase
    ) {
        this.addMessageUseCase = addMessageUseCase;

        isMessageEmptyMutableLiveData.setValue(true);

        final LiveData<ChatInfo> chatInfoLiveData = Transformations.switchMap(
            workmateIdMutableLiveData, workmateId -> getChatInfoUseCase.get(workmateId)
        );
        viewState.addSource(chatInfoLiveData, chatInfo -> combine(chatInfo, isMessageEmptyMutableLiveData.getValue(), scrollBottomButtonVisibilityMutableLiveData.getValue()));
        viewState.addSource(isMessageEmptyMutableLiveData, isMessageEmpty -> combine(chatInfoLiveData.getValue(), isMessageEmpty, scrollBottomButtonVisibilityMutableLiveData.getValue()));
        viewState.addSource(Transformations.distinctUntilChanged(scrollBottomButtonVisibilityMutableLiveData), isScrollBottomVisible -> combine(chatInfoLiveData.getValue(), isMessageEmptyMutableLiveData.getValue(), isScrollBottomVisible));
    }

    private void combine(
        @Nullable ChatInfo chatInfo,
        @Nullable Boolean isMessageEmpty,
        @Nullable Boolean isScrollBottomButtonVisible
    ) {
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
            !isMessageEmpty ? 1f : .75f,
            Objects.equals(isScrollBottomButtonVisible, true)
        ));
    }

    @NonNull
    LiveData<ChatViewState> getViewState() {
        return viewState;
    }

    @NonNull
    public LiveData<Integer> getScrollToPositionEvent() {
        return scrollToPositionEvent;
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
    }

    void onMessageChanged(@NonNull String message) {
        isMessageEmptyMutableLiveData.setValue(isMessageEmpty(message));
    }

    void onSendButtonClick(@NonNull String workmateId, @NonNull String messageToSend) {
        if (!isMessageEmpty(messageToSend)) {
            addMessageUseCase.add(workmateId, messageToSend);
            clearInputEvent.call();
        }
    }

    void onMessageListItemCountCalled(int itemCount) {
        if (itemCount != currentItemCount) {
            currentItemCount = itemCount; // Update flag

            if (!isScrollBottomButtonVisible) {
                // Auto scrolling only if the message list is already at the bottom
                // when the scroll bottom button is not visible
                scrollToLastMessage();
            }
        }
    }

    void onMessageListScrolled(int lastVisibleItemPosition) {
        isScrollBottomButtonVisible = lastVisibleItemPosition != currentItemCount - 1;
        scrollBottomButtonVisibilityMutableLiveData.setValue(isScrollBottomButtonVisible);
    }

    void onScrollBottomButtonClicked() {
        scrollToLastMessage();
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

    private void scrollToLastMessage() {
        scrollToPositionEvent.setValue(currentItemCount - 1);
    }
}
