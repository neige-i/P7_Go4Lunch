package com.neige_i.go4lunch.view.home;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.gps.GetGpsDialogUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class HomeViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private HomeViewModel homeViewModel;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetLocationPermissionUseCase getLocationPermissionUseCaseMock = mock(GetLocationPermissionUseCase.class);
    private final SetLocationPermissionUseCase setLocationPermissionUseCaseMock = mock(SetLocationPermissionUseCase.class);
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCaseMock = mock(SetLocationUpdatesUseCase.class);
    private final GetGpsDialogUseCase getGpsDialogUseCaseMock = mock(GetGpsDialogUseCase.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> isPermissionGrantedMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(isPermissionGrantedMutableLiveData).when(getLocationPermissionUseCaseMock).isGranted();
        doReturn(resolvableMutableLiveData).when(getGpsDialogUseCaseMock).showDialog();

        // Init ViewModel
        homeViewModel = new HomeViewModel(
            getLocationPermissionUseCaseMock,
            setLocationPermissionUseCaseMock,
            setLocationUpdatesUseCaseMock,
            getGpsDialogUseCaseMock
        );
    }

    // -------------------------------------- LOCATION TESTS ---------------------------------------

    @Test
    public void enableLocationPermissionAndUpdates_when_locationPermissionIsGranted() {
        // WHEN
        homeViewModel.onRequestLocationPermissionResult(true);

        // THEN
        verify(setLocationPermissionUseCaseMock).set(true);
        verify(setLocationUpdatesUseCaseMock).set(true);
        verifyNoMoreInteractions(setLocationPermissionUseCaseMock, setLocationUpdatesUseCaseMock);
    }

    @Test
    public void disableLocationPermissionAndUpdates_when_locationPermissionIsDenied() {
        // WHEN
        homeViewModel.onRequestLocationPermissionResult(false);

        // THEN
        verify(setLocationPermissionUseCaseMock).set(false);
        verify(setLocationUpdatesUseCaseMock).set(false);
        verifyNoMoreInteractions(setLocationPermissionUseCaseMock, setLocationUpdatesUseCaseMock);
    }

    @Test
    public void enableLocationPermissionAndUpdates_when_activityIsResumedWithGrantedLocationPermission() {
        // WHEN
        homeViewModel.onActivityResumed(true);

        // THEN
        verify(setLocationPermissionUseCaseMock).set(true);
        verify(setLocationUpdatesUseCaseMock).set(true);
        verifyNoMoreInteractions(setLocationPermissionUseCaseMock, setLocationUpdatesUseCaseMock);
    }

    @Test
    public void disableLocationPermissionAndUpdates_when_activityIsResumedWithDeniedLocationPermission() {
        // WHEN
        homeViewModel.onActivityResumed(false);

        // THEN
        verify(setLocationPermissionUseCaseMock).set(false);
        verify(setLocationUpdatesUseCaseMock).set(false);
        verifyNoMoreInteractions(setLocationPermissionUseCaseMock, setLocationUpdatesUseCaseMock);
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

    // ASKME: how to test that location permission is not requested if it has already been denied
    @Test
    public void requestLocationPermission_when_locationPermissionIsDenied() throws InterruptedException {
        // GIVEN
        isPermissionGrantedMutableLiveData.setValue(false);

        // WHEN
        final Void requestLocationPermission = getOrAwaitValue(homeViewModel.getRequestLocationPermissionEvent());

        // THEN
        assertNull(requestLocationPermission); // The SingleLiveEvent's value has been set
    }

    // ----------------------------------- SHOW GPS DIALOG TESTS -----------------------------------

    @Test
    public void showGpsDialog_when_resolvableApiExceptionIsSet() throws InterruptedException {
        // GIVEN
        final ResolvableApiException expectedResolvable = mock(ResolvableApiException.class);
        resolvableMutableLiveData.setValue(expectedResolvable);

        // WHEN
        final ResolvableApiException actualResolvable = getOrAwaitValue(homeViewModel.getShowGpsDialogEvent());

        // THEN
        assertEquals(expectedResolvable, actualResolvable);
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