package com.neige_i.go4lunch.domain;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.LiveDataTestUtils;
import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GetFirestoreUserUseCaseImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private GetFirestoreUserUseCase getFirestoreUserUseCase;

    private final FirestoreRepository firestoreRepository = mock(FirestoreRepository.class);

    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    @Before
    public void setUp() {
        doReturn(userMutableLiveData).when(firestoreRepository).getUser(anyString());

        getFirestoreUserUseCase = new GetFirestoreUserUseCaseImpl(firestoreRepository);
    }

    @Test
    public void firestoreUser_exists() throws InterruptedException {
        // Given: set non-null User
        userMutableLiveData.setValue(new User());

        // When
        final boolean userExists = LiveDataTestUtils.getOrAwaitValue(getFirestoreUserUseCase.userAlreadyExists("USER_ID"));

        // Then
        assertTrue(userExists);
    }

    @Test
    public void firestoreUser_doesNotExist() throws InterruptedException {
        // Given: set null User
        userMutableLiveData.setValue(null);

        // When
        final boolean userExists = LiveDataTestUtils.getOrAwaitValue(getFirestoreUserUseCase.userAlreadyExists("USER_ID"));

        // Then
        assertFalse(userExists);
    }

    @Test
    public void firestoreUser_noResponse() {
        // Given: omit setValue

        // When
        final Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> getOrAwaitValue(getFirestoreUserUseCase.userAlreadyExists("USER_ID"))
        );

        // Then
        assertEquals("LiveData value was never set.", thrownException.getMessage());
    }
}