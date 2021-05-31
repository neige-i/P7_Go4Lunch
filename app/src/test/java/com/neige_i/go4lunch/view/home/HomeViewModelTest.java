package com.neige_i.go4lunch.view.home;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.StopLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    private final GetLocationPermissionUseCase mockGetLocationPermissionUseCase = mock(GetLocationPermissionUseCase.class);
    private final SetLocationPermissionUseCase mockSetLocationPermissionUseCase = mock(SetLocationPermissionUseCase.class);
    private final StopLocationUpdatesUseCase mockStopLocationUpdatesUseCase = mock(StopLocationUpdatesUseCase.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> isPermissionGrantedMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(isPermissionGrantedMutableLiveData).when(mockGetLocationPermissionUseCase).isPermissionGranted();

        // Init ViewModel
        homeViewModel = new HomeViewModel(mockGetLocationPermissionUseCase, mockSetLocationPermissionUseCase, mockStopLocationUpdatesUseCase);
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_enablePermission() {
        // WHEN
        homeViewModel.updateLocationPermission(true);

        // THEN
        verify(mockSetLocationPermissionUseCase).setPermission(true);
        verifyNoMoreInteractions(mockSetLocationPermissionUseCase);
    }

    @Test
    public void verify_denyPermission() {
        // WHEN
        homeViewModel.updateLocationPermission(false);

        // THEN
        verify(mockSetLocationPermissionUseCase).setPermission(false);
        verifyNoMoreInteractions(mockSetLocationPermissionUseCase);
    }

    @Test
    public void verify_stopUpdates() {
        // WHEN
        homeViewModel.removeLocationUpdates();

        // THEN
        verify(mockStopLocationUpdatesUseCase).stopUpdates();
        verifyNoMoreInteractions(mockStopLocationUpdatesUseCase);
    }

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Test
    public void displayTitleAndAddMapFragment_when_viewModelIsCreated() throws InterruptedException {
        // WHEN
        final int titleIdState = getOrAwaitValue(homeViewModel.getTitleIdState());
        final boolean addMapFragment = getOrAwaitValue(homeViewModel.getAddMapFragmentEvent());

        // THEN
        assertEquals(R.string.title_restaurant, titleIdState);
        assertTrue(addMapFragment);
    }

    // ------------------------------------ LOCATION PERMISSION ------------------------------------

    @Test
    public void requestLocationPermission_when_deniedOnce() throws InterruptedException {
        // GIVEN
        isPermissionGrantedMutableLiveData.setValue(false);

        // WHEN
        final boolean requestLocationPermission = getOrAwaitValue(homeViewModel.getRequestLocationPermissionEvent());

        // THEN
        assertTrue(requestLocationPermission);
    }

    // ------------------------------------------- TITLE -------------------------------------------

    @Test
    public void updateTitle_when_mapItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.onNavigationItemSelected(R.id.action_map);

        // WHEN
        final int titleId = getOrAwaitValue(homeViewModel.getTitleIdState());

        // THEN
        assertEquals(R.string.title_restaurant, titleId);
    }

    @Test
    public void updateTitle_when_restaurantItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.onNavigationItemSelected(R.id.action_restaurant);

        // WHEN
        final int titleId = getOrAwaitValue(homeViewModel.getTitleIdState());

        // Then: the appropriate String ID is set for the title
        assertEquals(R.string.title_restaurant, titleId);
    }

    @Test
    public void updateTitle_when_workmateItemIsSelected() throws InterruptedException {
        // GIVEN
        homeViewModel.onNavigationItemSelected(R.id.action_workmates);

        // WHEN
        final int titleId = getOrAwaitValue(homeViewModel.getTitleIdState());

        // Then: the appropriate String ID is set for the title
        assertEquals(R.string.title_workmates, titleId);
    }

    // ----------------------------------------- FRAGMENT ------------------------------------------

    @Test
    public void hideMapFragment_addRestaurantFragment_when_fragmentIsNew() throws InterruptedException {
        // GIVEN
        homeViewModel.onNavigationItemSelected(R.id.action_restaurant);

        // WHEN
        final String fragmentToHideTag = getOrAwaitValue(homeViewModel.getHideFragmentEvent());
        final boolean addRestaurantFragment = getOrAwaitValue(homeViewModel.getAddRestaurantFragmentEvent());

        // THEN
        assertEquals(HomeActivity.MAP_FRAGMENT_TAG, fragmentToHideTag);
        assertTrue(addRestaurantFragment);
    }

    @Test
    public void hideMapFragment_addWorkmateFragment_when_fragmentIsNew() throws InterruptedException {
        // GIVEN
        homeViewModel.onNavigationItemSelected(R.id.action_workmates);

        // WHEN
        final String fragmentToHideTag = getOrAwaitValue(homeViewModel.getHideFragmentEvent());
        final boolean addWorkmateFragment = getOrAwaitValue(homeViewModel.getAddWorkmateFragmentEvent());

        // THEN
        assertEquals(HomeActivity.MAP_FRAGMENT_TAG, fragmentToHideTag);
        assertTrue(addWorkmateFragment);
    }

    @Test
    public void hideMapFragment_addWorkmateFragment_showMapAgain_when_fragmentIsOld() throws InterruptedException {
        // GIVEN
        homeViewModel.onNavigationItemSelected(R.id.action_workmates);
        homeViewModel.onNavigationItemSelected(R.id.action_map);

        // WHEN
        final String fragmentToHideTag = getOrAwaitValue(homeViewModel.getHideFragmentEvent());
        final String fragmentToShow = getOrAwaitValue(homeViewModel.getShowFragmentEvent());

        // THEN
        assertEquals(HomeActivity.WORKMATE_FRAGMENT_TAG, fragmentToHideTag);
        assertEquals(HomeActivity.MAP_FRAGMENT_TAG, fragmentToShow);
    }
}