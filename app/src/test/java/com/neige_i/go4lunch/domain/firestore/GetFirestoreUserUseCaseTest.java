package com.neige_i.go4lunch.domain.firestore;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GetFirestoreUserUseCaseTest {

    // --------------------------------------- CLASS VARIABLE --------------------------------------

    private static final String EXPECTED_USER_ID = "user id";

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetFirestoreUserUseCase getFirestoreUserUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirestoreRepository mockFirestoreRepository = mock(FirestoreRepository.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<User> firestoreUserMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(firestoreUserMutableLiveData).when(mockFirestoreRepository).getUser(EXPECTED_USER_ID);

        // Init UseCase
        getFirestoreUserUseCase = new GetFirestoreUserUseCaseImpl(mockFirestoreRepository);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnTrue_when_firestoreUserExists() throws InterruptedException {
        // GIVEN
        firestoreUserMutableLiveData.setValue(new User());

        // WHEN
        final boolean isUserNotNull = getOrAwaitValue(getFirestoreUserUseCase.userAlreadyExists(EXPECTED_USER_ID));

        // THEN
        assertTrue(isUserNotNull);
    }

    @Test
    public void returnFalse_when_firestoreUserIsNull() throws InterruptedException {
        // GIVEN
        firestoreUserMutableLiveData.setValue(null);

        // WHEN
        final boolean isUserNotNull = getOrAwaitValue(getFirestoreUserUseCase.userAlreadyExists(EXPECTED_USER_ID));

        // THEN
        assertFalse(isUserNotNull);
    }
}