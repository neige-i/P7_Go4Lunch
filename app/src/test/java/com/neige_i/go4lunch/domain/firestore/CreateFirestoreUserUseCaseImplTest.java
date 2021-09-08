package com.neige_i.go4lunch.domain.firestore;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CreateFirestoreUserUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private CreateFirestoreUserUseCase createFirestoreUserUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirestoreRepository mockFirestoreRepository = mock(FirestoreRepository.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        createFirestoreUserUseCase = new CreateFirestoreUserUseCaseImpl(mockFirestoreRepository);
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_addUserWithTheSameArguments_when_userIsCreated() {
        // GIVEN
        final String expectedUserId = "user id";
        final User expectedUser = new User();

        // WHEN
        createFirestoreUserUseCase.createUser(expectedUserId, expectedUser);

        // THEN
        verify(mockFirestoreRepository).addUser(expectedUserId, expectedUser);
        verifyNoMoreInteractions(mockFirestoreRepository);
    }
}