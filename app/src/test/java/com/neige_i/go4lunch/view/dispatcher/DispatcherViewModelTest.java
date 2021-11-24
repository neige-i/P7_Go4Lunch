package com.neige_i.go4lunch.view.dispatcher;

import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCase;
import com.neige_i.go4lunch.view.dispatcher.DispatcherViewModel.ActivityToStart;

import org.junit.Rule;
import org.junit.Test;

public class DispatcherViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetAuthUseCase getAuthUseCaseMock = mock(GetAuthUseCase.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final DispatcherViewModel dispatcherViewModel = new DispatcherViewModel(getAuthUseCaseMock);

    // ------------------------------------- REDIRECTION TESTS -------------------------------------

    @Test
    public void redirectToHomeActivity_when_getValue_with_authenticatedUser() {
        // GIVEN
        doReturn(true).when(getAuthUseCaseMock).isAuthenticated();

        // WHEN
        final ActivityToStart activityToStart = getValueForTesting(dispatcherViewModel.getStartActivityEvent());

        // THEN
        assertEquals(ActivityToStart.HOME_ACTIVITY, activityToStart);
    }

    @Test
    public void redirectToAuthActivity_when_getValue_with_unauthenticatedUser() {
        // GIVEN
        doReturn(false).when(getAuthUseCaseMock).isAuthenticated();

        // WHEN
        final ActivityToStart activityToStart = getValueForTesting(dispatcherViewModel.getStartActivityEvent());

        // THEN
        assertEquals(ActivityToStart.AUTH_ACTIVITY, activityToStart);
    }
}