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
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCase;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HomeViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetLocationPermissionUseCase getLocationPermissionUseCaseMock = mock(GetLocationPermissionUseCase.class);
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCaseMock = mock(SetLocationUpdatesUseCase.class);
    private final ShowGpsDialogUseCase showGpsDialogUseCaseMock = mock(ShowGpsDialogUseCase.class);
    private final FreeResourcesUseCase freeResourcesUseCaseMock = mock(FreeResourcesUseCase.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private HomeViewModel homeViewModel;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(resolvableMutableLiveData).when(showGpsDialogUseCaseMock).getDialog();
        doReturn(true).when(getLocationPermissionUseCaseMock).isGranted();

        // Init ViewModel
        homeViewModel = new HomeViewModel(
            getLocationPermissionUseCaseMock,
            setLocationUpdatesUseCaseMock,
            showGpsDialogUseCaseMock,
            freeResourcesUseCaseMock
        );
    }

    // ----------------------------------- FREE RESOURCES TESTS ------------------------------------

    @Test
    public void freeResources_when_activityIsPaused() {
        // WHEN
        homeViewModel.onCleared();

        // THEN
        verify(freeResourcesUseCaseMock).execute();
        verifyNoMoreInteractions(freeResourcesUseCaseMock);
    }

    // ---------------------------------- LOCATION UPDATES TESTS -----------------------------------

    @Test
    public void enableLocationUpdates_when_activityIsResumedWithGrantedLocationPermission() {
        // GIVEN
        // Location permission is granted by default in @Before

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
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_map);
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 0), viewState);
    }

    @Test
    public void getViewState_when_restaurantItemIsSelected() throws InterruptedException {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_restaurant);
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 1), viewState);
    }

    @Test
    public void getViewState_when_workmateItemIsSelected() throws InterruptedException {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_workmates);
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