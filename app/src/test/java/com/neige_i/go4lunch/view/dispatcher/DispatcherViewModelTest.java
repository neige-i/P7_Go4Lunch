package com.neige_i.go4lunch.view.dispatcher;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.LiveDataTestUtils;
import com.neige_i.go4lunch.domain.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.view.auth.AuthActivity;
import com.neige_i.go4lunch.view.home.HomeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DispatcherViewModelTest {

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private DispatcherViewModel dispatcherViewModel;

    private final GetFirebaseUserUseCase getFirebaseUserUseCase = mock(GetFirebaseUserUseCase.class);

    @Before
    public void setUp() {
        dispatcherViewModel = new DispatcherViewModel(getFirebaseUserUseCase);
    }

    @Test
    public void onSignedInUserChecked_redirectToAuth() throws InterruptedException {
        // Given: FirebaseUser is null
        doReturn(null).when(getFirebaseUserUseCase).getFirebaseUser();

        // When: check if user is signed in
        dispatcherViewModel.onSignedInUserChecked();

        // Then: user is redirected to AuthActivity
        assertEquals(
            AuthActivity.class,
            LiveDataTestUtils.getOrAwaitValue(dispatcherViewModel.getStartActivityEvent())
        );
    }

    @Test
    public void onSignedInUserChecked_redirectToHome() throws InterruptedException {
        // Given: FirebaseUser is not null
        doReturn(mock(FirebaseUser.class)).when(getFirebaseUserUseCase).getFirebaseUser();

        // When: check if user is signed in
        dispatcherViewModel.onSignedInUserChecked();

        // Then: user is redirected to HomeActivity
        assertEquals(
            HomeActivity.class,
            LiveDataTestUtils.getOrAwaitValue(dispatcherViewModel.getStartActivityEvent())
        );
    }
}