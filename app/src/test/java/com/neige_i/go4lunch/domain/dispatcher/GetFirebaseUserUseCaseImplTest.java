package com.neige_i.go4lunch.domain.dispatcher;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GetFirebaseUserUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetFirebaseUserUseCase getFirebaseUserUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        getFirebaseUserUseCase = new GetFirebaseUserUseCaseImpl(mockFirebaseAuth);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnNull_when_firebaseUserDoesNotExist() {
        // GIVEN
        doReturn(null).when(mockFirebaseAuth).getCurrentUser();

        // WHEN

        // THEN
        assertNull(getFirebaseUserUseCase.getUser());
    }

    @Test
    public void returnUser_when_firebaseUserExists() {
        // GIVEN
        final FirebaseUser expectedUser = mock(FirebaseUser.class);
        doReturn(expectedUser).when(mockFirebaseAuth).getCurrentUser();

        // WHEN

        // THEN
        assertEquals(expectedUser, getFirebaseUserUseCase.getUser());
    }
}