package com.neige_i.go4lunch.view.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.gps.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;
import com.neige_i.go4lunch.view.SingleLiveEvent;

public class HomeViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final SetLocationPermissionUseCase setLocationPermissionUseCase;
    @NonNull
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCase;
    @NonNull
    private final GpsStateChangeReceiver gpsStateChangeReceiver;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<HomeViewState> homeViewState = new MutableLiveData<>();
    @NonNull
    private final MediatorSingleLiveEvent<Void> requestLocationPermissionEvent = new MediatorSingleLiveEvent<>();
    @NonNull
    private final MediatorSingleLiveEvent<ResolvableApiException> showGpsDialogEvent = new MediatorSingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Void> showBlockingDialogEvent = new SingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Flag to avoid requesting the location permission repeatedly if the user denies it.
     */
    private boolean isLocationPermissionDenied;
    /**
     * Flag to automatically request the GPS dialog the first time location permission is granted.
     */
    private boolean isGpsAlreadyRequested;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public HomeViewModel(@NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
                         @NonNull SetLocationPermissionUseCase setLocationPermissionUseCase,
                         @NonNull SetLocationUpdatesUseCase setLocationUpdatesUseCase,
                         @NonNull ShowGpsDialogUseCase showGpsDialogUseCase,
                         @NonNull RequestGpsUseCase requestGpsUseCase,
                         @NonNull GpsStateChangeReceiver gpsStateChangeReceiver
    ) {
        Log.d("Neige", "HomeViewModel created");
        this.setLocationPermissionUseCase = setLocationPermissionUseCase;
        this.setLocationUpdatesUseCase = setLocationUpdatesUseCase;
        this.gpsStateChangeReceiver = gpsStateChangeReceiver;

        // Request the permission if it is not granted
        requestLocationPermissionEvent.addSource(getLocationPermissionUseCase.isGranted(), isPermissionGranted -> {
            if (isPermissionGranted) {
                isLocationPermissionDenied = false; // Reset flag

                // Request the GPS the first time location permission is granted
                if (!isGpsAlreadyRequested) {
                    requestGpsUseCase.request();
                    isGpsAlreadyRequested = true;
                }
            } else if (isLocationPermissionDenied) {
                showBlockingDialogEvent.call(); // TODO: is displayed twice if user exits and opens the app again
            } else {
                requestLocationPermissionEvent.call();
                isLocationPermissionDenied = true;
            }
        });

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

    @NonNull
    public GpsStateChangeReceiver getGpsStateChangeReceiver() {
        return gpsStateChangeReceiver;
    }

    // ------------------------------------- LOCATION METHODS --------------------------------------

    public void onPermissionChecked(boolean locationPermission) {
        setLocationPermissionUseCase.set(locationPermission);
        setLocationUpdatesUseCase.set(locationPermission);
    }

    public void onActivityPaused() {
        setLocationUpdatesUseCase.set(false);
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
}
