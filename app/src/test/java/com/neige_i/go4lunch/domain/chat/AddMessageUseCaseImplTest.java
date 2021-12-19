package com.neige_i.go4lunch.domain.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.Message;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class AddMessageUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    private final Clock fixedClock = Clock.fixed(Instant.EPOCH.plusMillis(MILLIS), ZoneId.systemDefault());

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private AddMessageUseCase addMessageUseCase;

    // ------------------------------------------- CONST -------------------------------------------

    private static final long MILLIS = 20;
    private static final String ROOM_ID = "roomId";
    private static final String CURRENT_USER_ID = "currentUserId";
    private static final String WORKMATE_ID = "workmateId";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);
        doReturn(firebaseUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn(CURRENT_USER_ID).when(firebaseUserMock).getUid();
        doReturn(ROOM_ID).when(firestoreRepositoryMock).getRoomId(CURRENT_USER_ID, WORKMATE_ID);

        // Init UseCase
        addMessageUseCase = new AddMessageUseCaseImpl(firestoreRepositoryMock, firebaseAuthMock, fixedClock);
    }

    @Test
    public void addMessageToFirestore_when_add_with_nonNullFirebaseUser() {
        // WHEN
        addMessageUseCase.add(WORKMATE_ID, "Hi!");

        // THEN
        verify(firestoreRepositoryMock).getRoomId(CURRENT_USER_ID, WORKMATE_ID);
        verify(firestoreRepositoryMock).addMessage(
            new Message(
                ROOM_ID,
                "Hi!",
                MILLIS,
                CURRENT_USER_ID
            )
        );
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_add_with_nullFirebaseUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        addMessageUseCase.add(WORKMATE_ID, "Hi!");

        // THEN
        verify(firestoreRepositoryMock, never()).getRoomId(anyString(), anyString());
        verify(firestoreRepositoryMock, never()).addMessage(any(Message.class));
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }
}