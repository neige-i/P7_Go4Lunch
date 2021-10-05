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
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.gps.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCase;
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
    private final SetLocationPermissionUseCase setLocationPermissionUseCaseMock = mock(SetLocationPermissionUseCase.class);
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCaseMock = mock(SetLocationUpdatesUseCase.class);
    private final ShowGpsDialogUseCase showGpsDialogUseCaseMock = mock(ShowGpsDialogUseCase.class);
    private final RequestGpsUseCase requestGpsUseCaseMock = mock(RequestGpsUseCase.class);
    private final GpsStateChangeReceiver gpsStateChangeReceiverMock = mock(GpsStateChangeReceiver.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> isPermissionGrantedMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(isPermissionGrantedMutableLiveData).when(getLocationPermissionUseCaseMock).isGranted();
        doReturn(resolvableMutableLiveData).when(showGpsDialogUseCaseMock).getDialog();

        // Init ViewModel
        homeViewModel = new HomeViewModel(
            getLocationPermissionUseCaseMock,
            setLocationPermissionUseCaseMock,
            setLocationUpdatesUseCaseMock,
            showGpsDialogUseCaseMock,
            requestGpsUseCaseMock,
            gpsStateChangeReceiverMock
        );
    }

    // -------------------------------------- LOCATION TESTS ---------------------------------------

    @Test
    public void enableLocationPermissionAndUpdates_when_locationPermissionIsGranted() {
        // WHEN
        homeViewModel.onPermissionChecked(true);

        // THEN
        verify(setLocationPermissionUseCaseMock).set(true);
        verify(setLocationUpdatesUseCaseMock).set(true);
        verifyNoMoreInteractions(setLocationPermissionUseCaseMock, setLocationUpdatesUseCaseMock);
    }

    @Test
    public void disableLocationPermissionAndUpdates_when_locationPermissionIsDenied() {
        // WHEN
        homeViewModel.onPermissionChecked(false);

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

    @Test
    public void requestLocationPermissionOnce_when_locationPermissionIsDeniedMultipleTimes() {
        // GIVEN
        isPermissionGrantedMutableLiveData.setValue(false);
        final int[] called = new int[]{0};

        // WHEN
        homeViewModel.getRequestLocationPermissionEvent().observeForever(unused -> {
            called[0] = called[0] += 1;

            if (called[0] > 1) {
                fail();
            }

            isPermissionGrantedMutableLiveData.setValue(false); // Deny permission again
        });

        // THEN
        assertEquals(1, called[0]); // The permission is only requested once
    }

    // ------------------------------------- REQUEST GPS TESTS -------------------------------------

    @Test
    public void requestGpsOnce_when_locationPermissionIsGrantedTheFirstTime() {
        // GIVEN
        isPermissionGrantedMutableLiveData.setValue(true);

        // WHEN
        homeViewModel.getRequestLocationPermissionEvent().observeForever(unused -> {
        });
        isPermissionGrantedMutableLiveData.setValue(true); // Grant the permission again

        // THEN
        verify(requestGpsUseCaseMock).request(); // The GPS is only requested once
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
    public void showBlockingDialog_when_locationPermissionIsDeniedMoreThanOnce() throws InterruptedException {
        // GIVEN
        isPermissionGrantedMutableLiveData.setValue(false);
        homeViewModel.getRequestLocationPermissionEvent().observeForever(unused -> {
        });

        // WHEN
        isPermissionGrantedMutableLiveData.setValue(false); // Deny the permission again
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

    // ---------------------------------- RECEIVER INSTANCE TESTS ----------------------------------


    @Test
    public void returnReceiverInstance_when_isRequired() {
        // WHEN
        final GpsStateChangeReceiver actualReceiver = homeViewModel.getGpsStateChangeReceiver();

        // THEN
        assertEquals(gpsStateChangeReceiverMock, actualReceiver);
    }
}