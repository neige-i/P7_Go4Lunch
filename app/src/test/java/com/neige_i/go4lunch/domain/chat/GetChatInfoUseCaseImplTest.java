package com.neige_i.go4lunch.domain.chat;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.Message;
import com.neige_i.go4lunch.data.firestore.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetChatInfoUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    private final ZoneId gmtZoneId = ZoneId.of("GMT");

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetChatInfoUseCase getChatInfoUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<List<Message>> messagesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> workmateMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- CONST -------------------------------------------

    private static final String ROOM_ID = "roomId";
    private static final String CURRENT_USER_ID = "currentUserId";
    private static final String WORKMATE_ID = "workmateId";
    private static final String WORKMATE_NAME = "workmateName";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);
        doReturn(firebaseUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn(CURRENT_USER_ID).when(firebaseUserMock).getUid();
        doReturn(ROOM_ID).when(firestoreRepositoryMock).getRoomId(CURRENT_USER_ID, WORKMATE_ID);
        doReturn(messagesMutableLiveData).when(firestoreRepositoryMock).getMessagesByRoomId(ROOM_ID);
        doReturn(workmateMutableLiveData).when(firestoreRepositoryMock).getUser(WORKMATE_ID);

        // Init UseCase
        getChatInfoUseCase = new GetChatInfoUseCaseImpl(firestoreRepositoryMock, firebaseAuthMock, gmtZoneId);

        // Default behaviour
        workmateMutableLiveData.setValue(new User("ID", "email", WORKMATE_NAME, null, null, null));
        messagesMutableLiveData.setValue(Arrays.asList(
            new Message("ID", "text3", 5 * 60 * 1000, CURRENT_USER_ID),
            new Message("ID", "text2", 60 * 1000 + 2, CURRENT_USER_ID),
            new Message("ID", "text0", 0, WORKMATE_ID),
            new Message("ID", "text1", 60 * 1000, WORKMATE_ID)
        ));
    }

    @Test
    public void returnChatInfo_when_getValue() {
        // WHEN
        final ChatInfo chatInfo = getValueForTesting(getChatInfoUseCase.get(WORKMATE_ID));

        // THEN
        assertEquals(
            new ChatInfo(
                WORKMATE_NAME,
                Arrays.asList(
                    // Messages are sorted by date
                    new ChatInfo.MessageInfo("text0", "01/01/1970 00:00", false),
                    new ChatInfo.MessageInfo("text1", "01/01/1970 00:01", false),
                    new ChatInfo.MessageInfo("text2", "01/01/1970 00:01", true),
                    new ChatInfo.MessageInfo("text3", "01/01/1970 00:05", true)
                )
            ),
            chatInfo
        );
    }

    @Test
    public void returnChatInfo_when_getValue_with_noMessages() {
        // GIVEN
        messagesMutableLiveData.setValue(Collections.emptyList());

        // WHEN
        final ChatInfo chatInfo = getValueForTesting(getChatInfoUseCase.get(WORKMATE_ID));

        // THEN
        assertEquals(
            new ChatInfo(
                WORKMATE_NAME,
                Collections.emptyList()
            ),
            chatInfo
        );
    }

    @Test
    public void returnChatInfo_when_getValue_with_unavailableMessages() {
        // GIVEN
        messagesMutableLiveData.setValue(null);

        // WHEN
        final ChatInfo chatInfo = getValueForTesting(getChatInfoUseCase.get(WORKMATE_ID));

        // THEN
        assertEquals(
            new ChatInfo(
                WORKMATE_NAME,
                Collections.emptyList()
            ),
            chatInfo
        );
    }

    @Test
    public void doNothing_when_getValue_with_unavailableWorkmate() {
        // GIVEN
        doReturn(null).when(firestoreRepositoryMock).getUser(WORKMATE_ID);

        // WHEN
        final int chatInfoTrigger = getLiveDataTriggerCount(getChatInfoUseCase.get(WORKMATE_ID));

        // THEN
        assertEquals(0, chatInfoTrigger);
    }

    @Test
    public void doNothing_when_getValue_with_nullFirebaseUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();
        getChatInfoUseCase = new GetChatInfoUseCaseImpl(firestoreRepositoryMock, firebaseAuthMock, gmtZoneId);

        // WHEN
        final int chatInfoTrigger = getLiveDataTriggerCount(getChatInfoUseCase.get(WORKMATE_ID));

        // THEN
        assertEquals(0, chatInfoTrigger);
    }
}