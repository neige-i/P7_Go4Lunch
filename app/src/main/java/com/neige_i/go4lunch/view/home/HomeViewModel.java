package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.gps.GetGpsDialogUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;

import java.util.Objects;

public class HomeViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final SetLocationPermissionUseCase setLocationPermissionUseCase;
    @NonNull
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCase;
    @NonNull
    private final GetGpsDialogUseCase getGpsDialogUseCase;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<HomeViewState> homeViewState = new MutableLiveData<>();
    @NonNull
    private final MediatorSingleLiveEvent<Void> requestLocationPermissionEvent = new MediatorSingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Flag to avoid requesting the location permission repeatedly if the user denies it.
     */
    private boolean isLocationPermissionAlreadyDenied;
    @Nullable
    private Boolean currentLocationPermission; // ASKME: Stateful=problem?

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public HomeViewModel(@NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
                         @NonNull SetLocationPermissionUseCase setLocationPermissionUseCase,
                         @NonNull SetLocationUpdatesUseCase setLocationUpdatesUseCase,
                         @NonNull GetGpsDialogUseCase getGpsDialogUseCase
    ) {
        this.setLocationPermissionUseCase = setLocationPermissionUseCase;
        this.setLocationUpdatesUseCase = setLocationUpdatesUseCase;
        this.getGpsDialogUseCase = getGpsDialogUseCase;

        // Request the permission if it is not granted
        requestLocationPermissionEvent.addSource(getLocationPermissionUseCase.isGranted(), isPermissionGranted -> {
            currentLocationPermission = isPermissionGranted;
            if (isPermissionGranted) {
                isLocationPermissionAlreadyDenied = false; // Reset flag
            } else if (!isLocationPermissionAlreadyDenied) {
                requestLocationPermissionEvent.call();
                isLocationPermissionAlreadyDenied = true;
            }
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

    // ---------------------------------------- GPS METHODS ----------------------------------------

    @NonNull
    public LiveData<ResolvableApiException> getShowGpsDialogEvent() {
        return getGpsDialogUseCase.showDialog();
    }

    // ------------------------------------- LOCATION METHODS --------------------------------------

    public void onActivityResumed(boolean locationPermission) {
        // Check if the location permission has changed and update the repositories if so
        if (!Objects.equals(currentLocationPermission, locationPermission)) {
            setLocationPermissionAndUpdates(locationPermission);
        }
    }

    public void onRequestLocationPermissionResult(boolean locationPermission) {
        setLocationPermissionAndUpdates(locationPermission);
    }

    private void setLocationPermissionAndUpdates(boolean locationPermission) {
        setLocationPermissionUseCase.set(locationPermission);
        setLocationUpdatesUseCase.set(locationPermission);
    }

    public void onActivityPaused() {
        setLocationUpdatesUseCase.set(false);
    }

    // ---------------------------------------- UI METHODS -----------------------------------------

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
}
