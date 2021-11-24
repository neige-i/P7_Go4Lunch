package com.neige_i.go4lunch.view.home;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
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
    public void freeResources_when_clearViewModel() {
        // WHEN
        homeViewModel.onCleared();

        // THEN
        verify(freeResourcesUseCaseMock).execute();
        verifyNoMoreInteractions(freeResourcesUseCaseMock);
    }

    // ---------------------------------- LOCATION UPDATES TESTS -----------------------------------

    @Test
    public void enableLocationUpdates_when_resumeActivity_with_grantedLocationPermission() {
        // GIVEN (Location permission is granted by default in @Before)

        // WHEN
        homeViewModel.onActivityResumed();

        // THEN
        verify(setLocationUpdatesUseCaseMock).set(true);
        verifyNoMoreInteractions(setLocationUpdatesUseCaseMock);
    }

    @Test
    public void disableLocationUpdates_when_resumeActivity_with_deniedLocationPermission() {
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
    public void requestLocationPermissionOnce_when_resumeActivityMultipleTimes() {
        // GIVEN
        doReturn(false).when(getLocationPermissionUseCaseMock).isGranted();

        // WHEN
        homeViewModel.onActivityResumed(); // Resumed once
        homeViewModel.onActivityResumed(); // Resumed twice
        final int requestLocationPermissionTrigger = getLiveDataTriggerCount(homeViewModel.getRequestLocationPermissionEvent());

        // THEN
        assertEquals(1, requestLocationPermissionTrigger);
    }

    // ----------------------------------- SHOW GPS DIALOG TESTS -----------------------------------

    @Test
    public void returnGpsDialog_when_getDialogEvent_with_expectedValue() {
        // GIVEN
        final ResolvableApiException expectedGpsDialog = mock(ResolvableApiException.class);
        resolvableMutableLiveData.setValue(expectedGpsDialog);

        // WHEN
        final ResolvableApiException actualGpsDialog = getValueForTesting(homeViewModel.getShowGpsDialogEvent());

        // THEN
        assertEquals(expectedGpsDialog, actualGpsDialog);
    }

    // ----------------------------------- BLOCKING DIALOG TESTS -----------------------------------

    @Test
    public void showBlockingDialog_when_resumeActivityMultipleTimes_with_deniedLocationPermission() {
        // GIVEN
        doReturn(false).when(getLocationPermissionUseCaseMock).isGranted();

        // WHEN
        homeViewModel.onActivityResumed(); // Resumed once
        homeViewModel.onActivityResumed(); // Resumed twice
        final int blockingDialogTrigger = getLiveDataTriggerCount(homeViewModel.getShowBlockingDialogEvent());

        // THEN
        assertEquals(1, blockingDialogTrigger);
    }

    // ---------------------------- BOTTOM NAVIGATION ITEM CLICK TESTS -----------------------------

    @Test
    public void returnViewState_when_selectMapItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_map);
        final HomeViewState viewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 0), viewState);
    }

    @Test
    public void returnViewState_when_selectRestaurantItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_restaurant);
        final HomeViewState viewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_restaurant, 1), viewState);
    }

    @Test
    public void returnViewState_when_selectWorkmateItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_workmates);
        final HomeViewState viewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(new HomeViewState(R.string.title_workmates, 2), viewState);
    }

    @Test(expected = RuntimeException.class)
    public void throwError_when_selectUnknownItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(-1);
        final int viewStateTrigger = getLiveDataTriggerCount(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(0, viewStateTrigger);
    }
}