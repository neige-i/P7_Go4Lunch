package com.neige_i.go4lunch.data.firestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class FirestoreRepositoryImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirebaseFirestore firebaseFirestoreMock = mock(FirebaseFirestore.class);
    private final Clock clock_12_12_2021 = Clock.fixed(
        LocalDate.of(2021, 12, 12).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private FirestoreRepository firestoreRepository;

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final CollectionReference userCollectionReferenceMock = mock(CollectionReference.class);
    private final CollectionReference messageCollectionReferenceMock = mock(CollectionReference.class);
    private final DocumentReference documentReferenceMock = mock(DocumentReference.class);
    private final Query queryMock = mock(Query.class);
    private final DocumentSnapshot documentSnapshotMock = mock(DocumentSnapshot.class);
    private final QuerySnapshot userQuerySnapshotMock = mock(QuerySnapshot.class);
    private final QuerySnapshot messageQuerySnapshotMock = mock(QuerySnapshot.class);

    // ------------------------------------- ARGUMENT CAPTORS --------------------------------------

    private final ArgumentCaptor<EventListener<DocumentSnapshot>> documentSnapshotListenerCaptor = ArgumentCaptor.forClass(EventListener.class);
    private final ArgumentCaptor<EventListener<QuerySnapshot>> querySnapshotListenerCaptor = ArgumentCaptor.forClass(EventListener.class);

    // ------------------------------------------- CONST -------------------------------------------

    private static final String USER_ID = "user ID";
    private static final String PLACE_ID = "PLACE_ID";
    private static final String ROOM_ID = "ROOM_ID";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(userCollectionReferenceMock).when(firebaseFirestoreMock).collection(FirestoreRepositoryImpl.USER_COLLECTION);
        doReturn(messageCollectionReferenceMock).when(firebaseFirestoreMock).collection(FirestoreRepositoryImpl.MESSAGE_COLLECTION);
        doReturn(documentReferenceMock).when(userCollectionReferenceMock).document(USER_ID);
        doReturn(getDefaultUser(0)).when(documentSnapshotMock).toObject(User.class);
        doReturn(queryMock).when(userCollectionReferenceMock).whereEqualTo("selectedRestaurant.id", PLACE_ID);
        doReturn(queryMock).when(queryMock).whereEqualTo("selectedRestaurant.date", "12/12/2021");
        doReturn(queryMock).when(messageCollectionReferenceMock).whereEqualTo("roomId", ROOM_ID);

        // Init repository
        firestoreRepository = new FirestoreRepositoryImpl(
            firebaseFirestoreMock,
            clock_12_12_2021
        );
    }

    // ------------------------------------- SINGLE USER TESTS -------------------------------------

    @Test
    public void returnUser_when_getUser_with_nonNullSnapshot() {
        // GIVEN
        final User[] actualUser = new User[1];

        // WHEN
        firestoreRepository.getUser(USER_ID).observeForever(user -> {
            actualUser[0] = user;
        });

        // Capture SnapshotListener
        verify(documentReferenceMock).addSnapshotListener(documentSnapshotListenerCaptor.capture());
        documentSnapshotListenerCaptor.getValue().onEvent(documentSnapshotMock, null);

        // THEN
        assertEquals(getDefaultUser(0), actualUser[0]);
    }

    @Test
    public void returnFirestoreUser_when_getUser_with_nullSnapshot() {
        // GIVEN
        final User[] actualUser = new User[1];

        // WHEN
        firestoreRepository.getUser(USER_ID).observeForever(user -> {
            actualUser[0] = user;
        });

        // Capture SnapshotListener
        verify(documentReferenceMock).addSnapshotListener(documentSnapshotListenerCaptor.capture());
        documentSnapshotListenerCaptor.getValue().onEvent(null, null); // Null snapshot

        // THEN
        assertNull(actualUser[0]);
    }

    @Test
    public void setUser_when_addUser() {
        // WHEN
        firestoreRepository.addUser(USER_ID, getDefaultUser(0));

        // THEN
        verify(documentReferenceMock).set(getDefaultUser(0));
        verifyNoMoreInteractions(documentReferenceMock);
    }

    // ------------------------------------ MULTIPLE USER TESTS ------------------------------------

    @Test
    public void returnFilteredUsers_when_getWorkmates_with_nonNullSnapshot() {
        // GIVEN
        doReturn(Arrays.asList(getDefaultUser(4), getDefaultUser(5))).when(userQuerySnapshotMock).toObjects(User.class);
        final List<User>[] actualUsers = new List[1];

        // WHEN
        firestoreRepository.getWorkmatesEatingAt(PLACE_ID).observeForever(users -> {
            actualUsers[0] = users;
        });

        // Capture SnapshotListener
        verify(queryMock).addSnapshotListener(querySnapshotListenerCaptor.capture());
        querySnapshotListenerCaptor.getValue().onEvent(userQuerySnapshotMock, null);

        // THEN
        assertEquals(
            Arrays.asList(getDefaultUser(4), getDefaultUser(5)),
            actualUsers[0]
        );
    }

    @Test
    public void returnNullUserCollection_when_getWorkmates_with_nullSnapshot() {
        // GIVEN
        doReturn(Arrays.asList(getDefaultUser(4), getDefaultUser(5))).when(userQuerySnapshotMock).toObjects(User.class);
        final List<User>[] actualUsers = new List[1];

        // WHEN
        firestoreRepository.getWorkmatesEatingAt(PLACE_ID).observeForever(users -> {
            actualUsers[0] = users;
        });

        // Capture SnapshotListener
        verify(queryMock).addSnapshotListener(querySnapshotListenerCaptor.capture());
        querySnapshotListenerCaptor.getValue().onEvent(null, null); // Null snapshot

        // THEN
        assertNull(actualUsers[0]);
    }

    @Test
    public void returnUserCollection_when_getAllUsers_with_nonNullSnapshot() {
        // GIVEN
        doReturn(getDefaultUserList()).when(userQuerySnapshotMock).toObjects(User.class);
        final List<User>[] actualUsers = new List[1];

        // WHEN
        firestoreRepository.getAllUsers().observeForever(users -> {
            actualUsers[0] = users;
        });

        // Capture SnapshotListener
        verify(userCollectionReferenceMock).addSnapshotListener(querySnapshotListenerCaptor.capture());
        querySnapshotListenerCaptor.getValue().onEvent(userQuerySnapshotMock, null);

        // THEN
        assertEquals(getDefaultUserList(), actualUsers[0]);
    }

    @Test
    public void returnNullUserCollection_when_getAllUsers_with_nullSnapshot() {
        // GIVEN
        doReturn(getDefaultUserList()).when(userQuerySnapshotMock).toObjects(User.class);
        final List<User>[] actualUsers = new List[1];

        // WHEN
        firestoreRepository.getAllUsers().observeForever(users -> {
            actualUsers[0] = users;
        });

        // Capture SnapshotListener
        verify(userCollectionReferenceMock).addSnapshotListener(querySnapshotListenerCaptor.capture());
        querySnapshotListenerCaptor.getValue().onEvent(null, null); // Null snapshot

        // THEN
        assertNull(actualUsers[0]);
    }

    // ------------------------------- RESTAURANT PREFERENCES TESTS --------------------------------

    @Test
    public void updateFavoriteRestaurantWithArrayUnion_when_addFavoriteRestaurant() {
        // WHEN
        firestoreRepository.addToFavoriteRestaurant(USER_ID, PLACE_ID);

        // THEN
        verify(documentReferenceMock).update(eq("favoriteRestaurants"), refEq(FieldValue.arrayUnion(PLACE_ID)));
        verifyNoMoreInteractions(documentReferenceMock);
    }

    @Test
    public void updateFavoriteRestaurantWithArrayRemove_when_removeFavoriteRestaurant() {
        // WHEN
        firestoreRepository.removeFromFavoriteRestaurant(USER_ID, PLACE_ID);

        // THEN
        verify(documentReferenceMock).update(eq("favoriteRestaurants"), refEq(FieldValue.arrayRemove(PLACE_ID)));
        verifyNoMoreInteractions(documentReferenceMock);
    }

    @Test
    public void updateSelectedRestaurantWithNonNullValues_when_setSelectedRestaurant() {
        // WHEN
        firestoreRepository.setSelectedRestaurant(
            USER_ID,
            PLACE_ID,
            "RESTAURANT_NAME",
            "RESTAURANT_ADDRESS"
        );

        // THEN
        verify(documentReferenceMock).update(
            "selectedRestaurant.id", PLACE_ID,
            "selectedRestaurant.date", "12/12/2021",
            "selectedRestaurant.name", "RESTAURANT_NAME",
            "selectedRestaurant.address", "RESTAURANT_ADDRESS"
        );
        verifyNoMoreInteractions(documentReferenceMock);
    }

    @Test
    public void updateSelectedRestaurantWithNullValue_when_clearSelectedRestaurant() {
        // WHEN
        firestoreRepository.clearSelectedRestaurant(USER_ID);

        // THEN
        verify(documentReferenceMock).update("selectedRestaurant", null);
        verifyNoMoreInteractions(documentReferenceMock);
    }

    // --------------------------------------- MESSAGE TESTS ---------------------------------------

    @Test
    public void returnRoomId_when_getValue_with_firstUserIdIsLowerThanSecondUserId() {
        // WHEN
        final String roomId = firestoreRepository.getRoomId("aaa", "bbb");

        // THEN
        assertEquals("aaabbb", roomId);
    }

    @Test
    public void returnRoomId_when_getValue_with_firstUserIdIsGreaterThanSecondUserId() {
        // WHEN
        final String roomId = firestoreRepository.getRoomId("bbb", "aaa");

        // THEN
        assertEquals("aaabbb", roomId);
    }

    @Test
    public void returnFilteredMessages_when_getMessagesById_with_nonNullSnapshot() {
        // GIVEN
        doReturn(Arrays.asList(getDefaultMessage(4), getDefaultMessage(5))).when(messageQuerySnapshotMock).toObjects(Message.class);
        final List<Message>[] actualMessages = new List[1];

        // WHEN
        firestoreRepository.getMessagesByRoomId(ROOM_ID).observeForever(messages -> {
            actualMessages[0] = messages;
        });

        // Capture SnapshotListener
        verify(queryMock).addSnapshotListener(querySnapshotListenerCaptor.capture());
        querySnapshotListenerCaptor.getValue().onEvent(messageQuerySnapshotMock, null);

        // THEN
        assertEquals(
            Arrays.asList(getDefaultMessage(4), getDefaultMessage(5)),
            actualMessages[0]
        );
    }

    @Test
    public void returnNullMessageCollection_when_getMessagesById_with_nullSnapshot() {
        // GIVEN
        doReturn(Arrays.asList(getDefaultMessage(3), getDefaultMessage(2))).when(userQuerySnapshotMock).toObjects(Message.class);
        final List<Message>[] actualMessages = new List[1];

        // WHEN
        firestoreRepository.getMessagesByRoomId(ROOM_ID).observeForever(messages -> {
            actualMessages[0] = messages;
        });

        // Capture SnapshotListener
        verify(queryMock).addSnapshotListener(querySnapshotListenerCaptor.capture());
        querySnapshotListenerCaptor.getValue().onEvent(null, null); // Null snapshot

        // THEN
        assertNull(actualMessages[0]);
    }

    @Test
    public void addMessageToCollection_when_addMessage() {
        // WHEN
        firestoreRepository.addMessage(getDefaultMessage(0));

        // THEN
        verify(messageCollectionReferenceMock).add(new Message(ROOM_ID + 0, "text0", 0, "ID0"));
        verifyNoMoreInteractions(messageCollectionReferenceMock);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private List<User> getDefaultUserList() {
        return Arrays.asList(getDefaultUser(0), getDefaultUser(1), getDefaultUser(2));
    }

    @NonNull
    private User getDefaultUser(int index) {
        return new User(
            USER_ID + index,
            "email" + index,
            "name" + index,
            "photo" + index,
            new User.SelectedRestaurant(PLACE_ID + index, "10/12/2021", "restaurant" + index, "address" + index),
            Arrays.asList(PLACE_ID + (10 + index), PLACE_ID + (20 + index))
        );
    }

    @NonNull
    private Message getDefaultMessage(int index) {
        return new Message(
            ROOM_ID + index,
            "text" + index,
            index,
            "ID" + index
        );
    }
}