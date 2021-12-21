package com.neige_i.go4lunch.view.chat;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.chat.AddMessageUseCase;
import com.neige_i.go4lunch.domain.chat.ChatInfo;
import com.neige_i.go4lunch.domain.chat.GetChatInfoUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetChatInfoUseCase getChatInfoUseCaseMock = mock(GetChatInfoUseCase.class);
    private final AddMessageUseCase addMessageUseCaseMock = mock(AddMessageUseCase.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private ChatViewModel chatViewModel;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<ChatInfo> chatInfoMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- CONST -------------------------------------------

    private static final String WORKMATE_ID = "workmateId";
    private static final String WORKMATE_NAME = "workmateName";
    private static final String MESSAGE_TEXT = "messageText";
    private static final String MESSAGE_DATE_TIME = "messageDateTime";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(chatInfoMutableLiveData).when(getChatInfoUseCaseMock).get(WORKMATE_ID);

        // Init ViewModel
        chatViewModel = new ChatViewModel(getChatInfoUseCaseMock, addMessageUseCaseMock);

        // Default behaviour
        chatInfoMutableLiveData.setValue(new ChatInfo(WORKMATE_NAME, getDefaultMessageList()));

        // Retrieve workmate ID when activity is created
        chatViewModel.onActivityCreated(WORKMATE_ID);
    }

    @Test
    public void returnState_when_getViewState_with_messages() {
        // WHEN
        final ChatViewState chatViewState = getValueForTesting(chatViewModel.getViewState());

        // THEN
        assertEquals(
            new ChatViewState(
                WORKMATE_NAME,
                getDefaultMessageViewStateList(),
                false,
                false,
                .75f,
                false
            ),
            chatViewState
        );
    }

    @Test
    public void returnState_when_getViewState_with_noMessages() {
        // GIVEN
        chatInfoMutableLiveData.setValue(new ChatInfo(
            WORKMATE_NAME,
            Collections.emptyList() // No messages
        ));

        // WHEN
        final ChatViewState chatViewState = getValueForTesting(chatViewModel.getViewState());

        // THEN
        assertEquals(
            new ChatViewState(
                WORKMATE_NAME,
                Collections.emptyList(),
                true, // "Empty" text is visible
                false,
                .75f,
                false
            ),
            chatViewState
        );
    }

    @Test
    public void returnState_when_getViewState_with_NonEmptyInputText() {
        // GIVEN
        chatViewModel.onMessageChanged("message to send");

        // WHEN
        final ChatViewState chatViewState = getValueForTesting(chatViewModel.getViewState());

        // THEN
        assertEquals(
            new ChatViewState(
                WORKMATE_NAME,
                getDefaultMessageViewStateList(),
                false,
                true, // Send button is enabled
                1, // Send button is fully opaque
                false
            ),
            chatViewState
        );
    }

    @Test
    public void returnState_when_getViewState_with_emptyInputText() {
        // GIVEN
        chatViewModel.onMessageChanged("      ");

        // WHEN
        final ChatViewState chatViewState = getValueForTesting(chatViewModel.getViewState());

        // THEN
        assertEquals(
            new ChatViewState(
                WORKMATE_NAME,
                getDefaultMessageViewStateList(),
                false,
                false, // Send button is disabled
                .75f, // Send button is partially transparent
                false
            ),
            chatViewState
        );
    }

    @Test
    public void returnState_when_getViewState_with_lastMessageNotVisible() {
        // GIVEN
        chatViewModel.onMessageListItemCountCalled(10); // 10 messages are in the list

        // WHEN
        chatViewModel.onMessageListScrolled(8);
        final ChatViewState chatViewState = getValueForTesting(chatViewModel.getViewState());

        // THEN
        assertEquals(
            new ChatViewState(
                WORKMATE_NAME,
                getDefaultMessageViewStateList(),
                false,
                false,
                .75f,
                true // Scroll bottom button is visible
            ),
            chatViewState
        );
    }

    @Test
    public void returnState_when_getViewState_with_lastMessageIsVisible() {
        // GIVEN
        chatViewModel.onMessageListItemCountCalled(10); // 10 messages are in the list

        // WHEN
        chatViewModel.onMessageListScrolled(9); // The 9th item is the last one in the list
        final ChatViewState chatViewState = getValueForTesting(chatViewModel.getViewState());

        // THEN
        assertEquals(
            new ChatViewState(
                WORKMATE_NAME,
                getDefaultMessageViewStateList(),
                false,
                false,
                .75f,
                false // Scroll bottom button is not visible
            ),
            chatViewState
        );
    }

    @Test
    public void doNothing_when_getViewState_with_noChatInfo() {
        // GIVEN
        chatInfoMutableLiveData.setValue(null);

        // WHEN
        final int viewStateTrigger = getLiveDataTriggerCount(chatViewModel.getViewState());

        // THEN
        assertEquals(0, viewStateTrigger);
    }

    @Test
    public void scrollToBottom_when_callItemCount_with_differentValue_and_currentlyAtTheBottom() {
        // GIVEN

        // WHEN
        chatViewModel.onMessageListItemCountCalled(10); // Initial value is 0
        final int scrollToPositionEvent = getValueForTesting(chatViewModel.getScrollToPositionEvent());

        // THEN
        assertEquals(9, scrollToPositionEvent); // Scroll to last item
    }

    @Test
    public void doNotScrollToBottom_when_callItemCount_with_differentValue_and_currentlyNotAtTheBottom() {
        // GIVEN
        chatViewModel.onMessageListScrolled(8); // The last item of the list is not visible

        // WHEN
        chatViewModel.onMessageListItemCountCalled(10); // Initial value is 0
        final int scrollToBottomTrigger = getLiveDataTriggerCount(chatViewModel.getScrollToPositionEvent());

        // THEN
        assertEquals(0, scrollToBottomTrigger);
    }

    @Test
    public void doNothing_when_callItemCount_with_sameValue() {
        // WHEN
        chatViewModel.onMessageListItemCountCalled(0); // Initial value is 0
        final int scrollToBottomTrigger = getLiveDataTriggerCount(chatViewModel.getScrollToPositionEvent());

        // THEN
        assertEquals(0, scrollToBottomTrigger);
    }

    @Test
    public void addMessageAndClearInput_when_clickOnSendButton_with_nonEmptyMessageText() {
        // WHEN
        chatViewModel.onSendButtonClick(WORKMATE_ID, "hey");
        final int clearInputTrigger = getLiveDataTriggerCount(chatViewModel.getClearInputEvent());

        // THEN
        verify(addMessageUseCaseMock).add(WORKMATE_ID, "hey");
        verifyNoMoreInteractions(addMessageUseCaseMock);
        assertEquals(1, clearInputTrigger);
    }

    @Test
    public void doNothing_when_clickOnSendButton_with_emptyMessageText() {
        // WHEN
        chatViewModel.onSendButtonClick(WORKMATE_ID, "      ");
        final int clearInputTrigger = getLiveDataTriggerCount(chatViewModel.getClearInputEvent());

        // THEN
        verify(addMessageUseCaseMock, never()).add(anyString(), anyString());
        verifyNoMoreInteractions(addMessageUseCaseMock);
        assertEquals(0, clearInputTrigger);
    }

    @Test
    public void scrollToBottom_when_clickOnScrollBottomButton() {
        // GIVEN
        chatViewModel.onMessageListItemCountCalled(20);

        // WHEN
        chatViewModel.onScrollBottomButtonClicked();
        final int scrollToPositionEvent = getValueForTesting(chatViewModel.getScrollToPositionEvent());

        // THEN
        assertEquals(19, scrollToPositionEvent); // Go to last item
    }

    @Test
    public void exitActivity_when_clickOnUpButton() {
        // WHEN
        chatViewModel.onMenuItemClick(android.R.id.home);
        final int goBackTrigger = getLiveDataTriggerCount(chatViewModel.getGoBackEvent());

        // THEN
        assertEquals(1, goBackTrigger);
    }

    @Test
    public void doNothing_when_clickOnOtherMenuItem() {
        // WHEN
        chatViewModel.onMenuItemClick(-1);
        final int goBackTrigger = getLiveDataTriggerCount(chatViewModel.getGoBackEvent());

        // THEN
        assertEquals(0, goBackTrigger);
    }

    @NonNull
    private List<ChatInfo.MessageInfo> getDefaultMessageList() {
        return Arrays.asList(
            getDefaultMessage(0, true),
            getDefaultMessage(1, false),
            getDefaultMessage(2, false),
            getDefaultMessage(3, true)
        );
    }

    @NonNull
    private ChatInfo.MessageInfo getDefaultMessage(int index, boolean isCurrentUserSender) {
        return new ChatInfo.MessageInfo(
            MESSAGE_TEXT + index,
            MESSAGE_DATE_TIME + index,
            isCurrentUserSender
        );
    }

    @NonNull
    private List<ChatViewState.MessageViewState> getDefaultMessageViewStateList() {
        return Arrays.asList(
            getDefaultMessageViewState(0, R.color.orange_light, 1, 100, 0),
            getDefaultMessageViewState(1, R.color.gray_light, 0, 0, 100),
            getDefaultMessageViewState(2, R.color.gray_light, 0, 0, 100),
            getDefaultMessageViewState(3, R.color.orange_light, 1, 100, 0)
        );
    }

    @NonNull
    private ChatViewState.MessageViewState getDefaultMessageViewState(
        int index,
        int backgroundColor,
        float horizontalBias,
        int marginStart,
        int marginEnd
    ) {
        return new ChatViewState.MessageViewState(
            MESSAGE_TEXT + index,
            MESSAGE_DATE_TIME + index,
            backgroundColor,
            horizontalBias,
            marginStart,
            marginEnd
        );
    }
}