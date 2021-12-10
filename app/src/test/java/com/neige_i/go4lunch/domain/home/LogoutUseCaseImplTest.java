package com.neige_i.go4lunch.domain.home;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;

public class LogoutUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final LogoutUseCase logoutUseCase = new LogoutUseCaseImpl(firebaseAuthMock);

    // --------------------------------------- LOGOUT TESTS ----------------------------------------

    @Test
    public void signOutFromFirebase_when_logout() {
        // WHEN
        logoutUseCase.logout();

        // THEN
        verify(firebaseAuthMock).signOut();
        verifyNoMoreInteractions(firebaseAuthMock);
    }
}