package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCase;
import com.neige_i.go4lunch.domain.home.GetAutocompleteResultsUseCase;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class HomeViewModel extends ViewModel {

    private static final int SEARCH_QUERY_THRESHOLD = 3;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetLocationPermissionUseCase getLocationPermissionUseCase;
    @NonNull
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCase;
    @NonNull
    private final GetAutocompleteResultsUseCase getAutocompleteResultsUseCase;
    @NonNull
    private final FreeResourcesUseCase freeResourcesUseCase;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MediatorLiveData<HomeViewState> homeViewState = new MediatorLiveData<>();
    @NonNull
    private final SingleLiveEvent<Void> requestLocationPermissionEvent = new SingleLiveEvent<>();
    @NonNull
    private final MediatorSingleLiveEvent<ResolvableApiException> showGpsDialogEvent = new MediatorSingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Void> showBlockingDialogEvent = new SingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final MutableLiveData<Integer> menuItemMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> searchQueryMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MediatorLiveData<List<AutocompleteRestaurant>> autocompleteResultMediatorLiveData = new MediatorLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isViewReadyMutableLiveData = new MutableLiveData<>();
    /**
     * Flag to avoid requesting the location permission repeatedly if the user denies it.
     */
    private boolean isLocationPermissionAlreadyDenied;
    private boolean newSearchQuery;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    HomeViewModel(
        @NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
        @NonNull SetLocationUpdatesUseCase setLocationUpdatesUseCase,
        @NonNull ShowGpsDialogUseCase showGpsDialogUseCase,
        @NonNull GetAutocompleteResultsUseCase getAutocompleteResultsUseCase,
        @NonNull FreeResourcesUseCase freeResourcesUseCase
    ) {
        this.getLocationPermissionUseCase = getLocationPermissionUseCase;
        this.setLocationUpdatesUseCase = setLocationUpdatesUseCase;
        this.getAutocompleteResultsUseCase = getAutocompleteResultsUseCase;
        this.freeResourcesUseCase = freeResourcesUseCase;

        // Retrieve the GPS dialog from the UseCase and prompt it to the user with a SingleLiveEvent
        showGpsDialogEvent.addSource(showGpsDialogUseCase.getDialog(), resolvableApiException -> {
            showGpsDialogEvent.setValue(resolvableApiException);
        });

        homeViewState.addSource(menuItemMutableLiveData, menuItemId -> combine(menuItemId, searchQueryMutableLiveData.getValue(), autocompleteResultMediatorLiveData.getValue(), isViewReadyMutableLiveData.getValue()));
        homeViewState.addSource(searchQueryMutableLiveData, searchQuery -> combine(menuItemMutableLiveData.getValue(), searchQuery, autocompleteResultMediatorLiveData.getValue(), isViewReadyMutableLiveData.getValue()));
        homeViewState.addSource(autocompleteResultMediatorLiveData, autocompleteRestaurants -> combine(menuItemMutableLiveData.getValue(), searchQueryMutableLiveData.getValue(), autocompleteRestaurants, isViewReadyMutableLiveData.getValue()));
        homeViewState.addSource(isViewReadyMutableLiveData, isViewReady -> combine(menuItemMutableLiveData.getValue(), searchQueryMutableLiveData.getValue(), autocompleteResultMediatorLiveData.getValue(), isViewReady));

        // Set default page to display
        onBottomNavigationItemClicked(R.id.action_map);
    }

    private void combine(
        @Nullable Integer menuItemId,
        @Nullable String searchQuery,
        @Nullable List<AutocompleteRestaurant> autocompleteRestaurants,
        @Nullable Boolean isViewReady
    ) {
        if (menuItemId == null || !Objects.equals(isViewReady, true)) {
            return;
        }

        final int titleId;
        final int viewPagerPosition;

        if (menuItemId == R.id.action_map) {
            titleId = R.string.title_restaurant;
            viewPagerPosition = 0;
        } else if (menuItemId == R.id.action_restaurant) {
            titleId = R.string.title_restaurant;
            viewPagerPosition = 1;
        } else if (menuItemId == R.id.action_workmates) {
            titleId = R.string.title_workmates;
            viewPagerPosition = 2;
        } else {
            throw new IllegalArgumentException("onBottomNavigationItemClicked() was called with a wrong MenuItem ID: " + menuItemId);
        }

        final boolean isSearchResultListVisible;
        if (searchQuery != null) {
            if (searchQuery.length() >= SEARCH_QUERY_THRESHOLD) {
                isSearchResultListVisible = true;

                if (newSearchQuery) {
                    newSearchQuery = false;
                    autocompleteResultMediatorLiveData.addSource(
                        getAutocompleteResultsUseCase.get(searchQuery), autocompleteResult -> {
                            autocompleteResultMediatorLiveData.setValue(autocompleteResult);
                        }
                    );
                }
            } else {
                isSearchResultListVisible = false;
            }
        } else {
            isSearchResultListVisible = false;
        }

        final List<AutocompleteViewState> autocompleteViewStates;
        if (autocompleteRestaurants != null) {
            newSearchQuery = false;
            autocompleteViewStates = autocompleteRestaurants.stream()
                .map(autocompleteRestaurant -> new AutocompleteViewState(
                    autocompleteRestaurant.getPlaceId(),
                    autocompleteRestaurant.getRestaurantName()
                ))
                .collect(Collectors.toList());
        } else {
            autocompleteViewStates = new ArrayList<>();
        }

        final boolean isSearchEnabled = viewPagerPosition != 2;

        homeViewState.setValue(new HomeViewState(
            titleId,
            viewPagerPosition,
            isSearchEnabled,
            isSearchEnabled && isSearchResultListVisible,
            autocompleteViewStates
        ));
    }

    @NonNull
    public LiveData<HomeViewState> getHomeViewState() {
        return homeViewState;
    }

    @NonNull
    public LiveData<Void> getRequestLocationPermissionEvent() {
        return requestLocationPermissionEvent;
    }

    @NonNull
    public LiveData<ResolvableApiException> getShowGpsDialogEvent() {
        return showGpsDialogEvent;
    }

    @NonNull
    public LiveData<Void> getShowBlockingDialogEvent() {
        return showBlockingDialogEvent;
    }

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCleared() {
        super.onCleared();

        freeResourcesUseCase.execute();
    }

    // ------------------------------------- LOCATION METHODS --------------------------------------

    public void onActivityResumed() {
        final boolean isLocationPermissionGranted = getLocationPermissionUseCase.isGranted();

        // Set location updates (enable it or not)
        setLocationUpdatesUseCase.set(isLocationPermissionGranted);

        // Setup permission & GPS requests
        if (isLocationPermissionGranted) {
            isLocationPermissionAlreadyDenied = false; // Reset flag
        } else if (isLocationPermissionAlreadyDenied) {
            showBlockingDialogEvent.call();
        } else {
            requestLocationPermissionEvent.call();
            isLocationPermissionAlreadyDenied = true;
        }
    }

    // --------------------------------- BOTTOM NAVIGATION METHODS ---------------------------------

    public void onBottomNavigationItemClicked(int menuItemId) {
        menuItemMutableLiveData.setValue(menuItemId);
    }

    public void onQueryTextChange(@NonNull String queryText) {
        newSearchQuery = true;
        searchQueryMutableLiveData.setValue(queryText);
//        if (queryText.length() >= 3) {
//        }
    }

    /**
     * Is useful to avoid NullPointerException because the view state is observed in onCreate to update
     * a MenuItem that is not initialized until onCreateOptionsMenu().
     */
    public void onOptionsMenuCreated() {
        isViewReadyMutableLiveData.setValue(true);
    }
}
