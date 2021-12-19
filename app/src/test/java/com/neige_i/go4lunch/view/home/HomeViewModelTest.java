package com.neige_i.go4lunch.view.home;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.repository.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.domain.home.DrawerInfo;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCase;
import com.neige_i.go4lunch.domain.home.GetAutocompleteResultsUseCase;
import com.neige_i.go4lunch.domain.home.GetDrawerInfoUseCase;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.home.LogoutUseCase;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.home.SetSearchQueryUseCase;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetLocationPermissionUseCase getLocationPermissionUseCaseMock = mock(GetLocationPermissionUseCase.class);
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCaseMock = mock(SetLocationUpdatesUseCase.class);
    private final ShowGpsDialogUseCase showGpsDialogUseCaseMock = mock(ShowGpsDialogUseCase.class);
    private final GetAutocompleteResultsUseCase getAutocompleteResultsUseCaseMock = mock(GetAutocompleteResultsUseCase.class);
    private final SetSearchQueryUseCase setSearchQueryUseCaseMock = mock(SetSearchQueryUseCase.class);
    private final GetDrawerInfoUseCase getDrawerInfoUseCaseMock = mock(GetDrawerInfoUseCase.class);
    private final FreeResourcesUseCase freeResourcesUseCaseMock = mock(FreeResourcesUseCase.class);
    private final LogoutUseCase logoutUseCaseMock = mock(LogoutUseCase.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private HomeViewModel homeViewModel;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AutocompleteRestaurant>> autocompleteRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<DrawerInfo> drawerInfoMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- CONST -------------------------------------------

    private static final String PHOTO = "PHOTO";
    private static final String NAME = "NAME";
    private static final String EMAIL = "EMAIL";
    private static final String PLACE_ID = "PLACE_ID";
    private static final String QUERY = "pizza";
    private static final String RESTAURANT_NAME = "RESTAURANT_NAME";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(resolvableMutableLiveData).when(showGpsDialogUseCaseMock).getDialog();
        doReturn(true).when(getLocationPermissionUseCaseMock).isGranted();
        doReturn(autocompleteRestaurantsMutableLiveData).when(getAutocompleteResultsUseCaseMock).get(anyString());
        doReturn(drawerInfoMutableLiveData).when(getDrawerInfoUseCaseMock).get();

        // Init ViewModel
        homeViewModel = new HomeViewModel(
            getLocationPermissionUseCaseMock,
            setLocationUpdatesUseCaseMock,
            showGpsDialogUseCaseMock,
            getAutocompleteResultsUseCaseMock,
            setSearchQueryUseCaseMock,
            getDrawerInfoUseCaseMock,
            freeResourcesUseCaseMock,
            logoutUseCaseMock
        );

        // Default behaviour
        drawerInfoMutableLiveData.setValue(new DrawerInfo(
            PHOTO,
            NAME,
            EMAIL,
            PLACE_ID
        ));
        homeViewModel.onSearchInitialized();
        autocompleteRestaurantsMutableLiveData.setValue(getDefaultAutocompleteRestaurantList());
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
    public void returnDefaultViewState_when_initializeViewModel() {
        // WHEN
        final HomeViewState viewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                null,
                Collections.emptyList(),
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            viewState
        );
    }

    @Test
    public void returnViewState_when_selectMapItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_map);
        final HomeViewState viewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                null,
                Collections.emptyList(),
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            viewState
        );
    }

    @Test
    public void returnViewState_when_selectRestaurantItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_restaurant);
        final HomeViewState viewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                1,
                true,
                null,
                Collections.emptyList(),
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            viewState
        );
    }

    @Test
    public void returnViewState_when_selectWorkmateItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_workmates);
        final HomeViewState viewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_workmates,
                2,
                false,
                null,
                Collections.emptyList(),
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            viewState
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwError_when_selectUnknownItem() {
        // WHEN
        homeViewModel.onBottomNavigationItemClicked(-1);
        final int viewStateTrigger = getLiveDataTriggerCount(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(0, viewStateTrigger);
    }

    // --------------------------------------- SEARCH TESTS ----------------------------------------

    @Test
    public void returnNullViewState_when_createActivity() {
        // WHEN
        homeViewModel.onActivityCreated();
        final HomeViewState homeViewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertNull(homeViewState);
    }

    @Test
    public void updateQueryOnly_when_inputText_with_noResponseFromAutocompleteApi() {
        // GIVEN
        autocompleteRestaurantsMutableLiveData.setValue(null);

        // WHEN
        homeViewModel.onQueryTextChange(QUERY);
        final HomeViewState homeViewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                QUERY, // Update search query
                Collections.emptyList(), // Empty suggestions
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            homeViewState
        );
    }

    @Test
    public void updateQueryAndSuggestions_when_inputText_with_responseFromAutocompleteApi() {
        // WHEN
        homeViewModel.onQueryTextChange(QUERY);
        final HomeViewState homeViewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                QUERY, // Update search query
                getDefaultAutocompleteRestaurantList(), // Update suggestions
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            homeViewState
        );
    }

    @Test
    public void requestAutocompleteResultsOnce_when_updateViewState_with_oldQuery() {
        // GIVEN
        homeViewModel.onQueryTextChange(QUERY); // Update view state once

        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_restaurant); // Update view state twice
        getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        verify(getAutocompleteResultsUseCaseMock).get(QUERY);
        verifyNoMoreInteractions(getAutocompleteResultsUseCaseMock);
    }

    @Test
    public void showEmptySuggestionsAndNotRequestAutocomplete_when_inputEmptyText() {
        // WHEN
        homeViewModel.onQueryTextChange("");
        final HomeViewState homeViewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                "", // Empty search query
                Collections.emptyList(), // Empty suggestions
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            homeViewState
        );
        verify(getAutocompleteResultsUseCaseMock, never()).get("");
        verifyNoMoreInteractions(getAutocompleteResultsUseCaseMock);
    }

    @Test
    public void collapseSearchView_when_navigateToWorkmateTab_with_expandedSearchView() {
        // GIVEN
        homeViewModel.onSearchMenuExpanded();

        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_workmates);
        getValueForTesting(homeViewModel.getHomeViewState());
        final int collapseTrigger = getLiveDataTriggerCount(homeViewModel.getCollapseSearchViewEvent());

        // THEN
        assertEquals(1, collapseTrigger);
    }

    @Test
    public void expandSearchViewAndRestoreQuery_when_navigateFromWorkmateTab_with_savedQuery() {
        // GIVEN
        homeViewModel.onSearchMenuExpanded();
        homeViewModel.onQueryTextChange("restaurant"); // Input text in SearchView
        homeViewModel.onBottomNavigationItemClicked(R.id.action_workmates);
        getValueForTesting(homeViewModel.getHomeViewState());

        // WHEN
        homeViewModel.onBottomNavigationItemClicked(R.id.action_map); // Change tab
        final HomeViewState homeViewState = getValueForTesting(homeViewModel.getHomeViewState());
        final int expandTrigger = getLiveDataTriggerCount(homeViewModel.getExpandSearchViewEvent());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                "restaurant", // Restored search query
                Collections.emptyList(),
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            homeViewState
        );
        assertEquals(1, expandTrigger);
    }

    @Test
    public void setEmptyQuery_when_collapseSearchView() {
        //WHEN
        homeViewModel.onSearchMenuCollapsed();
        final HomeViewState homeViewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                "", // Empty search query
                Collections.emptyList(),
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            homeViewState
        );
    }

    @Test
    public void closeSearchQuery_when_inputEmptyText() {
        // WHEN
        homeViewModel.onQueryTextChange("");

        // THEN
        verify(setSearchQueryUseCaseMock).close();
        verifyNoMoreInteractions(setSearchQueryUseCaseMock);
    }

    @Test
    public void hideSuggestionsAndStartSearchQuery_when_clickOnAutocompleteResult() {
        // WHEN
        homeViewModel.onAutocompleteResultClick(getDefaultAutocompleteRestaurant(0));
        final HomeViewState homeViewState = getValueForTesting(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(
            new HomeViewState(
                R.string.title_restaurant,
                0,
                true,
                RESTAURANT_NAME + 0, // Update search query
                Collections.emptyList(), // Empty suggestions
                PHOTO,
                NAME,
                EMAIL,
                PLACE_ID
            ),
            homeViewState
        );
        verify(setSearchQueryUseCaseMock).launch(getDefaultAutocompleteRestaurant(0));
        verifyNoMoreInteractions(setSearchQueryUseCaseMock);
    }

    // --------------------------------------- DRAWER TESTS ----------------------------------------

    @Test
    public void closeDrawer_when_selectAnyItem() {
        // WHEN
        homeViewModel.onDrawerItemSelected(-1); // Arbitrary value
        final int closeDrawerTrigger = getLiveDataTriggerCount(homeViewModel.getCloseDrawerEvent());

        // THEN
        assertEquals(1, closeDrawerTrigger);
    }

    @Test
    public void showLunch_when_selectLunchItem() {
        // WHEN
        homeViewModel.onDrawerItemSelected(R.id.lunch_menu);
        final int showLunchTrigger = getLiveDataTriggerCount(homeViewModel.getShowLunchEvent());

        // THEN
        assertEquals(1, showLunchTrigger);
    }

    @Test
    public void showSettings_when_selectSettingsItem() {
        // WHEN
        homeViewModel.onDrawerItemSelected(R.id.settings_menu);
        final int showSettingsTrigger = getLiveDataTriggerCount(homeViewModel.getShowSettingsEvent());

        // THEN
        assertEquals(1, showSettingsTrigger);
    }

    @Test
    public void exitActivityAndLogout_when_selectLogoutItem() {
        // WHEN
        homeViewModel.onDrawerItemSelected(R.id.logout_menu);
        final int logoutTrigger = getLiveDataTriggerCount(homeViewModel.getLogoutEvent());

        // THEN
        assertEquals(1, logoutTrigger);
        verify(logoutUseCaseMock).logout();
        verifyNoMoreInteractions(logoutUseCaseMock);
    }

    @Test
    public void closeDrawer_when_pressBackButtonWithOpenDrawer() {
        // WHEN
        homeViewModel.onBackPressed(true);
        final int closeDrawerTrigger = getLiveDataTriggerCount(homeViewModel.getCloseDrawerEvent());
        final int closeActivityTrigger = getLiveDataTriggerCount(homeViewModel.getCloseActivityEvent());

        // THEN
        assertEquals(1, closeDrawerTrigger);
        assertEquals(0, closeActivityTrigger);
    }

    @Test
    public void closeActivity_when_pressBackButtonWithClosedDrawer() {
        // WHEN
        homeViewModel.onBackPressed(false);
        final int closeDrawerTrigger = getLiveDataTriggerCount(homeViewModel.getCloseDrawerEvent());
        final int closeActivityTrigger = getLiveDataTriggerCount(homeViewModel.getCloseActivityEvent());

        // THEN
        assertEquals(0, closeDrawerTrigger);
        assertEquals(1, closeActivityTrigger);
    }

    @Test
    public void doNothing_when_getViewState_withNoDrawerInfo() {
        // GIVEN
        drawerInfoMutableLiveData.setValue(null);

        // WHEN
        final int viewStateTrigger = getLiveDataTriggerCount(homeViewModel.getHomeViewState());

        // THEN
        assertEquals(0, viewStateTrigger);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private List<AutocompleteRestaurant> getDefaultAutocompleteRestaurantList() {
        return Arrays.asList(
            getDefaultAutocompleteRestaurant(0),
            getDefaultAutocompleteRestaurant(1)
        );
    }

    @NonNull
    private AutocompleteRestaurant getDefaultAutocompleteRestaurant(int index) {
        return new AutocompleteRestaurant(
            PLACE_ID + index,
            RESTAURANT_NAME + index
        );
    }
}