package com.neige_i.go4lunch.view.home;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.location.GetGpsResolvableUseCase;
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
    private final GetGpsResolvableUseCase getGpsResolvableUseCaseMock = mock(GetGpsResolvableUseCase.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> isPermissionGrantedMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(isPermissionGrantedMutableLiveData).when(getLocationPermissionUseCaseMock).isGranted();
        doReturn(resolvableMutableLiveData).when(getGpsResolvableUseCaseMock).getResolvable();

        // Init ViewModel
        homeViewModel = new HomeViewModel(
            getLocationPermissionUseCaseMock,
            setLocationPermissionUseCaseMock,
            setLocationUpdatesUseCaseMock,
            getGpsResolvableUseCaseMock
        );
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_enableLocationPermissionAndUpdates_when_locationPermissionIsGranted() {
        // WHEN
        homeViewModel.setLocationPermissionAndUpdates(true);

        // THEN
        verify(setLocationPermissionUseCaseMock).set(true);
        verify(setLocationUpdatesUseCaseMock).set(true);
        verifyNoMoreInteractions(setLocationPermissionUseCaseMock, setLocationUpdatesUseCaseMock);
    }

    @Test
    public void verify_disableLocationPermissionAndUpdates_when_locationPermissionIsDenied() {
        // WHEN
        homeViewModel.setLocationPermissionAndUpdates(false);

        // THEN
        verify(setLocationPermissionUseCaseMock).set(false);
        verify(setLocationUpdatesUseCaseMock).set(false);
        verifyNoMoreInteractions(setLocationPermissionUseCaseMock, setLocationUpdatesUseCaseMock);
    }

    @Test
    public void verify_disableLocationUpdates_when_updatesNeedToBeStopped() {
        // WHEN
        homeViewModel.stopLocationUpdates();

        // THEN
        verify(setLocationUpdatesUseCaseMock).set(false);
        verifyNoMoreInteractions(setLocationUpdatesUseCaseMock);
    }

    // ----------------------------- REQUEST LOCATION PERMISSION TESTS -----------------------------

    @Test
    public void requestLocationPermission_when_locationPermissionIsDenied() throws InterruptedException {
        // GIVEN
        isPermissionGrantedMutableLiveData.setValue(false);

        // WHEN
        final Void requestLocationPermission = getOrAwaitValue(homeViewModel.getRequestLocationPermissionEvent());

        // THEN
        assertNull(requestLocationPermission); // The SingleLiveEvent's value has been set
    }

    // ------------------------------------- ENABLE GPS TESTS --------------------------------------

    @Test
    public void enableGpsSettings_when_viewModelIsCreatedAndGpsIsDisabled() throws InterruptedException {
        // GIVEN: GPS is considered disabled if ResolvableApiException is not null
        final ResolvableApiException expectedResolvable = mock(ResolvableApiException.class);
        resolvableMutableLiveData.setValue(expectedResolvable);

        // WHEN
        final ResolvableApiException actualResolvable = getOrAwaitValue(homeViewModel.getEnableGpsEvent());

        // THEN
        assertEquals(expectedResolvable, actualResolvable);
    }

    // -------------------------------------- VIEW STATE TESTS -------------------------------------

    @Test
    public void returnInitialViewState_when_viewModelIsCreated() throws InterruptedException {
        // WHEN
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 0), viewState);
    }

    @Test
    public void updateViewState_when_mapItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.setViewState(R.id.action_map);

        // WHEN
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 0), viewState);
    }

    @Test
    public void updateViewState_when_restaurantItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.setViewState(R.id.action_restaurant);

        // WHEN
        final HomeViewState viewState = getOrAwaitValue(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 1), viewState);
    }

    @Test
    public void updateViewState_when_workmateItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.setViewState(R.id.action_workmates);

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
            () -> homeViewModel.setViewState(-1)
        );

        // THEN
        assertEquals("setViewState() was called with a wrong MenuItem ID: -1", thrownException.getMessage());
    }
}