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
import com.neige_i.go4lunch.domain.home.SetSearchQueryUseCase;
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

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetLocationPermissionUseCase getLocationPermissionUseCase;
    @NonNull
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCase;
    @NonNull
    private final GetAutocompleteResultsUseCase getAutocompleteResultsUseCase;
    @NonNull
    private final SetSearchQueryUseCase setSearchQueryUseCase;
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
    @NonNull
    private final SingleLiveEvent<Void> collapseSearchViewEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<String> expandSearchViewEvent = new SingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final MutableLiveData<Integer> menuItemMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isSearchMenuItemReadyMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> searchQueryMutableLiveData = new MutableLiveData<>();
    @NonNull
    private final MediatorLiveData<List<AutocompleteRestaurant>> autocompleteRestaurantsMediatorLiveData = new MediatorLiveData<>();

    /**
     * Flag to avoid requesting the location permission repeatedly if the user denies it.
     */
    private boolean isLocationPermissionAlreadyDenied;
    private boolean newAutocompleteRequest;
    private boolean showAutocompleteSuggestions;
    @Nullable
    private String savedSearchQuery;
    private boolean isSearchMenuJustCollapsedOrExpanded;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    HomeViewModel(
        @NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
        @NonNull SetLocationUpdatesUseCase setLocationUpdatesUseCase,
        @NonNull ShowGpsDialogUseCase showGpsDialogUseCase,
        @NonNull GetAutocompleteResultsUseCase getAutocompleteResultsUseCase,
        @NonNull SetSearchQueryUseCase setSearchQueryUseCase,
        @NonNull FreeResourcesUseCase freeResourcesUseCase
    ) {
        this.getLocationPermissionUseCase = getLocationPermissionUseCase;
        this.setLocationUpdatesUseCase = setLocationUpdatesUseCase;
        this.getAutocompleteResultsUseCase = getAutocompleteResultsUseCase;
        this.setSearchQueryUseCase = setSearchQueryUseCase;
        this.freeResourcesUseCase = freeResourcesUseCase;

        // Retrieve the GPS dialog from the UseCase and prompt it to the user with a SingleLiveEvent
        showGpsDialogEvent.addSource(showGpsDialogUseCase.getDialog(), resolvableApiException -> {
            showGpsDialogEvent.setValue(resolvableApiException);
        });

        homeViewState.addSource(menuItemMutableLiveData, menuItemId -> combine(menuItemId, isSearchMenuItemReadyMutableLiveData.getValue(), searchQueryMutableLiveData.getValue(), autocompleteRestaurantsMediatorLiveData.getValue()));
        homeViewState.addSource(isSearchMenuItemReadyMutableLiveData, isViewReady -> combine(menuItemMutableLiveData.getValue(), isViewReady, searchQueryMutableLiveData.getValue(), autocompleteRestaurantsMediatorLiveData.getValue()));
        homeViewState.addSource(searchQueryMutableLiveData, searchQuery -> combine(menuItemMutableLiveData.getValue(), isSearchMenuItemReadyMutableLiveData.getValue(), searchQuery, autocompleteRestaurantsMediatorLiveData.getValue()));
        homeViewState.addSource(autocompleteRestaurantsMediatorLiveData, autocompleteRestaurants -> combine(menuItemMutableLiveData.getValue(), isSearchMenuItemReadyMutableLiveData.getValue(), searchQueryMutableLiveData.getValue(), autocompleteRestaurants));

        // Set default page to display
        onBottomNavigationItemClicked(R.id.action_map);
    }

    private void combine(
        @Nullable Integer menuItemId,
        @Nullable Boolean isViewReady,
        @Nullable String searchQuery,
        @Nullable List<AutocompleteRestaurant> autocompleteRestaurants
    ) {
        if (!Objects.equals(isViewReady, true)) {
            homeViewState.setValue(null);
            return;
        }

        if (menuItemId == null) {
            return;
        }

        // Setup activity title & ViewPager position
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

        // Save the current search query and collapse the SearchView
        if (menuItemId == R.id.action_workmates && searchQuery != null) {
            savedSearchQuery = searchQuery;
            collapseSearchViewEvent.call();
            return;
        }

        // Restore the previously saved search query and expand the SearchView
        if (menuItemId != R.id.action_workmates && savedSearchQuery != null) {
            searchQuery = savedSearchQuery;
            savedSearchQuery = null; // Reset flag
            expandSearchViewEvent.call();
            searchQueryMutableLiveData.setValue(searchQuery);
            return;
        }

        // Setup autocomplete request
        if (searchQuery != null && newAutocompleteRequest) {
            newAutocompleteRequest = false; // Reset flag

            autocompleteRestaurantsMediatorLiveData.addSource(
                getAutocompleteResultsUseCase.get(searchQuery), autocompleteSuggestions -> {
                    autocompleteRestaurantsMediatorLiveData.setValue(autocompleteSuggestions);
                }
            );
        }

        // Setup autocomplete results
        final List<String> autocompleteResults;
        if (searchQuery != null && autocompleteRestaurants != null && showAutocompleteSuggestions) {
            autocompleteResults = autocompleteRestaurants.stream()
                .map(autocompleteRestaurant -> autocompleteRestaurant.getRestaurantName())
                .collect(Collectors.toList());
        } else {
            autocompleteResults = new ArrayList<>();
        }

        homeViewState.setValue(new HomeViewState(
            titleId,
            viewPagerPosition,
            viewPagerPosition != 2,
            searchQuery,
            autocompleteResults
        ));
    }

    @NonNull
    LiveData<HomeViewState> getHomeViewState() {
        return homeViewState;
    }

    @NonNull
    LiveData<Void> getRequestLocationPermissionEvent() {
        return requestLocationPermissionEvent;
    }

    @NonNull
    LiveData<ResolvableApiException> getShowGpsDialogEvent() {
        return showGpsDialogEvent;
    }

    @NonNull
    LiveData<Void> getShowBlockingDialogEvent() {
        return showBlockingDialogEvent;
    }

    @NonNull
    LiveData<Void> getCollapseSearchViewEvent() {
        return collapseSearchViewEvent;
    }

    @NonNull
    LiveData<String> getExpandSearchViewEvent() {
        return expandSearchViewEvent;
    }

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    protected void onCleared() {
        super.onCleared();

        freeResourcesUseCase.execute();
    }

    // ------------------------------------- LOCATION METHODS --------------------------------------

    void onActivityResumed() {
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

    void onBottomNavigationItemClicked(int menuItemId) {
        menuItemMutableLiveData.setValue(menuItemId);
    }

    // -------------------------------------- SEARCH METHODS ---------------------------------------

    void onActivityCreated() {
        isSearchMenuItemReadyMutableLiveData.setValue(false);
    }

    /**
     * Is useful to avoid NullPointerException. Without it, the search MenuItem can be updated while
     * observing the view state in onCreate() before being initialized inside onCreateOptionsMenu().
     */
    void onSearchInitialized() {
        isSearchMenuItemReadyMutableLiveData.setValue(true);
    }

    void onQueryTextChange(@NonNull String queryText) {
        // This method is called with an empty String right after the SearchView being
        // collapsed or expanded when it previously contained a non-empty input text
        if (isSearchMenuJustCollapsedOrExpanded && queryText.equals("")) {
            isSearchMenuJustCollapsedOrExpanded = false; // Reset flag
            return;
        }

        // Prevent repeated calls: the SearchView updates the ViewState which updates the SearchView afterwards
        if (!Objects.equals(searchQueryMutableLiveData.getValue(), queryText)) {
            newAutocompleteRequest = true;
            showAutocompleteSuggestions = true;
            searchQueryMutableLiveData.setValue(queryText);
        }
    }

    void onSearchMenuExpanded() {
        isSearchMenuJustCollapsedOrExpanded = true;
    }

    void onSearchMenuCollapsed() {
        isSearchMenuJustCollapsedOrExpanded = true;
        searchQueryMutableLiveData.setValue(null);

        setSearchQueryUseCase.close();
    }

    void onAutocompleteResultClick(@NonNull String restaurantName) {
        // No need to execute a new search, only update the SearchView text field
        showAutocompleteSuggestions = false;
        searchQueryMutableLiveData.setValue(restaurantName);

        // Request filtering restaurants according to the restaurant name
        setSearchQueryUseCase.launch(restaurantName);
    }
}
