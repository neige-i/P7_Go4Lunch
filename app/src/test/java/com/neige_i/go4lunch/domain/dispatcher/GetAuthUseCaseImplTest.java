package com.neige_i.go4lunch.domain.dispatcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;

public class GetAuthUseCaseImplTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final GetAuthUseCase getAuthUseCase = new GetAuthUseCaseImpl(firebaseAuthMock);

    // ----------------------------------- AUTHENTICATION TESTS ------------------------------------

    @Test
    public void returnTrue_when_firebaseUserExists() {
        // GIVEN
        doReturn(mock(FirebaseUser.class)).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        final boolean isAuthenticated = getAuthUseCase.isAuthenticated();

        // THEN
        assertTrue(isAuthenticated);
    }

    @Test
    public void returnFalse_when_firebaseUserIsNull() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        final boolean isAuthenticated = getAuthUseCase.isAuthenticated();

        // THEN
        assertFalse(isAuthenticated);
    }
}