package com.neige_i.go4lunch.view.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCase;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;
import com.neige_i.go4lunch.view.SingleLiveEvent;

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
    private final FreeResourcesUseCase freeResourcesUseCase;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<HomeViewState> homeViewState = new MutableLiveData<>();
    @NonNull
    private final SingleLiveEvent<Void> requestLocationPermissionEvent = new SingleLiveEvent<>();
    @NonNull
    private final MediatorSingleLiveEvent<ResolvableApiException> showGpsDialogEvent = new MediatorSingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Void> showBlockingDialogEvent = new SingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Flag to avoid requesting the location permission repeatedly if the user denies it.
     */
    private boolean isLocationPermissionAlreadyDenied;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    HomeViewModel(
        @NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
        @NonNull SetLocationUpdatesUseCase setLocationUpdatesUseCase,
        @NonNull ShowGpsDialogUseCase showGpsDialogUseCase,
        @NonNull FreeResourcesUseCase freeResourcesUseCase
    ) {
        this.getLocationPermissionUseCase = getLocationPermissionUseCase;
        this.setLocationUpdatesUseCase = setLocationUpdatesUseCase;
        this.freeResourcesUseCase = freeResourcesUseCase;

        // Retrieve the GPS dialog from the UseCase and prompt it to the user with a SingleLiveEvent
        showGpsDialogEvent.addSource(showGpsDialogUseCase.getDialog(), resolvableApiException -> {
            showGpsDialogEvent.setValue(resolvableApiException);
        });
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

        homeViewState.setValue(new HomeViewState(titleId, viewPagerPosition));
    }

    public void onQueryTextChange(@NonNull String queryText) {
        Log.d("Neige", "onQueryTextChange: " + queryText);
    }
}
