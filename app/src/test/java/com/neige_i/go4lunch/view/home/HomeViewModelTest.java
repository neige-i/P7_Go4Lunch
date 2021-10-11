package com.neige_i.go4lunch.view.home;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.gps.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomeViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private HomeViewModel homeViewModel;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetLocationPermissionUseCase getLocationPermissionUseCaseMock = mock(GetLocationPermissionUseCase.class);
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCaseMock = mock(SetLocationUpdatesUseCase.class);
    private final ShowGpsDialogUseCase showGpsDialogUseCaseMock = mock(ShowGpsDialogUseCase.class);
    private final RequestGpsUseCase requestGpsUseCaseMock = mock(RequestGpsUseCase.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(true).when(getLocationPermissionUseCaseMock).isGranted();
        doReturn(resolvableMutableLiveData).when(showGpsDialogUseCaseMock).getDialog();

        // Init ViewModel
        homeViewModel = new HomeViewModel(
            getLocationPermissionUseCaseMock,
            setLocationUpdatesUseCaseMock,
            showGpsDialogUseCaseMock,
            requestGpsUseCaseMock
        );
    }

    // ---------------------------------- LOCATION UPDATES TESTS -----------------------------------

    @Test
    public void enableLocationUpdates_when_activityIsResumedWithGrantedLocationPermission() {
        // WHEN
        homeViewModel.onActivityResumed();

        // THEN
        verify(setLocationUpdatesUseCaseMock).set(true);
        verifyNoMoreInteractions(setLocationUpdatesUseCaseMock);
    }

    @Test
    public void disableLocationUpdates_when_activityIsResumedWithDeniedLocationPermission() {
        // GIVEN
        doReturn(false).when(getLocationPermissionUseCaseMock).isGranted();

        // WHEN
        homeViewModel.onActivityResumed();

        // THEN
        verify(setLocationUpdatesUseCaseMock).set(false);
        verifyNoMoreInteractions(setLocationUpdatesUseCaseMock);
    }

    @Test
    public void disableLocationUpdates_when_activityIsPaused() {
        // WHEN
        homeViewModel.onActivityPaused();

        // THEN
        verify(setLocationUpdatesUseCaseMock).set(false);
        verifyNoMoreInteractions(setLocationUpdatesUseCaseMock);
    }

    // ----------------------------- REQUEST LOCATION PERMISSION TESTS -----------------------------

    @Test
    public void requestLocationPermissionOnce_when_activityIsResumedMultipleTimes() {
        // GIVEN
        doReturn(false).when(getLocationPermissionUseCaseMock).isGranted();
        homeViewModel.onActivityResumed(); // Resumed once
        final int[] called = new int[]{0};

        // WHEN
        homeViewModel.getRequestLocationPermissionEvent().observeForever(unused -> {
            called[0] = called[0] += 1;

            if (called[0] > 1) {
                fail();
            }

            homeViewModel.onActivityResumed(); // Resumed twice
        });

        // THEN
        assertEquals(1, called[0]); // The permission is only requested once
    }

    // ------------------------------------- REQUEST GPS TESTS -------------------------------------

    @Test
    public void requestGpsOnce_when_activityIsResumedMultipleTimesWithGrantedLocationPermission() {
        // GIVEN
        homeViewModel.onActivityResumed(); // Resumed once

        // WHEN
        homeViewModel.onActivityResumed(); // Resumed twice

        // THEN
        verify(requestGpsUseCaseMock).request(); // The GPS is requested once
        verifyNoMoreInteractions(requestGpsUseCaseMock);
    }

    // ----------------------------------- SHOW GPS DIALOG TESTS -----------------------------------

    @Test
    public void returnGpsDialog_when_dialogIsRequested() throws InterruptedException {
        // GIVEN
        final ResolvableApiException expectedGpsDialog = mock(ResolvableApiException.class);
        resolvableMutableLiveData.setValue(expectedGpsDialog);

        // WHEN
        final ResolvableApiException actualGpsDialog = getOrAwaitValue(homeViewModel.getShowGpsDialogEvent());

        // THEN
        assertEquals(expectedGpsDialog, actualGpsDialog);
    }

    // ----------------------------------- BLOCKING DIALOG TESTS -----------------------------------

    @Test
    public void showBlockingDialog_when_activityIsResumedWithDeniedLocationPermissionMoreThanOnce() throws InterruptedException {
        // GIVEN
        doReturn(false).when(getLocationPermissionUseCaseMock).isGranted();
        homeViewModel.onActivityResumed(); // Resumed once

        // WHEN
        homeViewModel.onActivityResumed(); // Resumed twice
        final Void blockingDialog = getOrAwaitValue(homeViewModel.getShowBlockingDialogEvent());

        // THEN
        assertNull(blockingDialog); // Blocking dialog is showed
    }

    // ---------------------------- BOTTOM NAVIGATION ITEM CLICK TESTS -----------------------------

    @Test
    public void getViewState_when_mapItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_map);

        // WHEN
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 0), viewState);
    }

    @Test
    public void getViewState_when_restaurantItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_restaurant);

        // WHEN
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 1), viewState);
    }

    @Test
    public void getViewState_when_workmateItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_workmates);

        // WHEN
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_workmates, 2), viewState);
    }

    @Test
    public void throwError_when_viewStateIsSetWithWrongArgument() {
        // WHEN
        final Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> homeViewModel.onBottomNavigationItemClicked(-1)
        );

        // THEN
        assertEquals(
            "onBottomNavigationItemClicked() was called with a wrong MenuItem ID: -1",
            thrownException.getMessage()
        );
    }
}