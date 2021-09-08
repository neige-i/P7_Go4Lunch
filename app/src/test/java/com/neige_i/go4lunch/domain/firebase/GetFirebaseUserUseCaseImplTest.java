package com.neige_i.go4lunch.domain.firebase;

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

    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        getFirebaseUserUseCase = new GetFirebaseUserUseCaseImpl(firebaseAuthMock);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnUser_when_firebaseUserExists() {
        // GIVEN
        final FirebaseUser expectedUser = mock(FirebaseUser.class);
        doReturn(expectedUser).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        final FirebaseUser actualUser = getFirebaseUserUseCase.getUser();

        // THEN
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void returnNull_when_firebaseUserDoesNotExist() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        final FirebaseUser user = getFirebaseUserUseCase.getUser();

        // THEN
        assertNull(user);
    }
}