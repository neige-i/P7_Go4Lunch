package com.neige_i.go4lunch.view.dispatcher;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.view.dispatcher.DispatcherViewModel.ActivityToStart;

import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DispatcherViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetFirebaseUserUseCase mockGetFirebaseUserUseCase = mock(GetFirebaseUserUseCase.class);

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void redirectToAuthActivity_when_firebaseUserIsNull() throws InterruptedException {
        // GIVEN
        doReturn(null).when(mockGetFirebaseUserUseCase).getUser();

        // WHEN
        final DispatcherViewModel dispatcherViewModel = new DispatcherViewModel(mockGetFirebaseUserUseCase);
        final ActivityToStart activityToStart = getOrAwaitValue(dispatcherViewModel.getStartActivityEvent());

        // THEN
        assertEquals(ActivityToStart.AUTH_ACTIVITY, activityToStart);
    }

    @Test
    public void redirectToHomeActivity_when_firebaseUserIsNotNull() throws InterruptedException {
        // GIVEN
        doReturn(mock(FirebaseUser.class)).when(mockGetFirebaseUserUseCase).getUser();

        // WHEN
        final DispatcherViewModel dispatcherViewModel = new DispatcherViewModel(mockGetFirebaseUserUseCase);
        final ActivityToStart activityToStart = getOrAwaitValue(dispatcherViewModel.getStartActivityEvent());

        // THEN
        assertEquals(ActivityToStart.HOME_ACTIVITY, activityToStart);
    }
}