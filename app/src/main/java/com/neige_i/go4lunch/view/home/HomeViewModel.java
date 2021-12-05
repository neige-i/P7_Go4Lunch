package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCase;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.home.SetSearchQueryUseCase;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import java.util.Objects;

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

    /**
     * Flag to avoid requesting the location permission repeatedly if the user denies it.
     */
    private boolean isLocationPermissionAlreadyDenied;
    @Nullable
    private String currentQuerySearch;
    private boolean isSearViewExpanded;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    HomeViewModel(
        @NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
        @NonNull SetLocationUpdatesUseCase setLocationUpdatesUseCase,
        @NonNull ShowGpsDialogUseCase showGpsDialogUseCase,
        @NonNull SetSearchQueryUseCase setSearchQueryUseCase,
        @NonNull FreeResourcesUseCase freeResourcesUseCase
    ) {
        this.getLocationPermissionUseCase = getLocationPermissionUseCase;
        this.setLocationUpdatesUseCase = setLocationUpdatesUseCase;
        this.setSearchQueryUseCase = setSearchQueryUseCase;
        this.freeResourcesUseCase = freeResourcesUseCase;

        // Retrieve the GPS dialog from the UseCase and prompt it to the user with a SingleLiveEvent
        showGpsDialogEvent.addSource(showGpsDialogUseCase.getDialog(), resolvableApiException -> {
            showGpsDialogEvent.setValue(resolvableApiException);
        });

        homeViewState.addSource(menuItemMutableLiveData, menuItemId -> combine(menuItemId, isSearchMenuItemReadyMutableLiveData.getValue()));
        homeViewState.addSource(isSearchMenuItemReadyMutableLiveData, isViewReady -> combine(menuItemMutableLiveData.getValue(), isViewReady));

        // Set default page to display
        onBottomNavigationItemClicked(R.id.action_map);
    }

    private void combine(@Nullable Integer menuItemId, @Nullable Boolean isViewReady) {
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

        homeViewState.setValue(new HomeViewState(
            titleId,
            viewPagerPosition,
            viewPagerPosition != 2
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

    @NonNull
    public LiveData<Void> getCollapseSearchViewEvent() {
        return collapseSearchViewEvent;
    }

    @NonNull
    public LiveData<String> getExpandSearchViewEvent() {
        return expandSearchViewEvent;
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

        // Handle the search's visibility and content when navigating with the bottom navigation bar
        if (menuItemId == R.id.action_workmates) {
            if (!isSearViewExpanded) {
                currentQuerySearch = null; // Reset flag
            }

            collapseSearchViewEvent.call();
        } else if ((menuItemId == R.id.action_map || menuItemId == R.id.action_restaurant) &&
            currentQuerySearch != null
        ) {
            // Expand the search MenuItem with the previously saved query String
            expandSearchViewEvent.setValue(currentQuerySearch);
        }
    }

    // -------------------------------------- SEARCH METHODS ---------------------------------------

    /**
     * Is useful to avoid NullPointerException. Without it, the search MenuItem can be updated while
     * observing the view state in onCreate() before being initialized inside onCreateOptionsMenu().
     */
    public void onSearchMenuItemInitialized() {
        isSearchMenuItemReadyMutableLiveData.setValue(true);
    }

    public void onQueryTextChange(@NonNull String queryText) {
        setSearchQueryUseCase.launch(queryText);

        // This method is called with an empty String after the SearchView being
        // expanded the first time and after being collapsed
        // The following condition ignores the 'empty String' case when the SearchView is collapsed
        if (isSearViewExpanded) {
            currentQuerySearch = queryText;
        }
    }

    public void onSearchMenuExpanded() {
        isSearViewExpanded = true;
    }

    public void onSearchMenuCollapsed() {
        isSearViewExpanded = false;
        setSearchQueryUseCase.close();
    }
}
