package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.GetLocPermissionUseCase;
import com.neige_i.go4lunch.domain.StopLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.UpdateLocPermissionUseCase;
import com.neige_i.go4lunch.view.util.MediatorSingleLiveEvent;

import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_MAP;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_RESTAURANT;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_WORKMATE;

public class HomeViewModel extends ViewModel {

    @NonNull
    private final UpdateLocPermissionUseCase updateLocPermissionUseCase;
    @NonNull
    private final StopLocationUpdatesUseCase stopLocationUpdatesUseCase;

    @NonNull
    private final MutableLiveData<HomeViewState> viewState = new MutableLiveData<>();
    @NonNull
    private final MediatorSingleLiveEvent<Void> requestLocationPermissionEvent = new MediatorSingleLiveEvent<>();

    /**
     * Control variable to prevent infinite loop when denying location permission.<br />
     * 1. if onResume() is called -> update location permission.<br />
     * 2. if location permission is not granted -> display the request permission dialog.<br />
     * Then: permission not granted -> display request dialog -> permission denied ->
     * dialog dismissed -> activity resumed -> location updated -> display request dialog again ->
     * permission denied -> infinite loop
     */
    private boolean isLocationPermissionJustDenied;

    public HomeViewModel(@NonNull GetLocPermissionUseCase getLocPermissionUseCase,
                         @NonNull UpdateLocPermissionUseCase updateLocPermissionUseCase,
                         @NonNull StopLocationUpdatesUseCase stopLocationUpdatesUseCase
    ) {
        this.updateLocPermissionUseCase = updateLocPermissionUseCase;
        this.stopLocationUpdatesUseCase = stopLocationUpdatesUseCase;

        requestLocationPermissionEvent.addSource(getLocPermissionUseCase.isPermissionGranted(), isPermissionGranted -> {
            if (isPermissionGranted) {
                isLocationPermissionJustDenied = false;
            } else if (!isLocationPermissionJustDenied) {
                isLocationPermissionJustDenied = true;

                // Request the permission only if it is not currently granted and if the user has not just denied it
                requestLocationPermissionEvent.call();
            }
        });

        // Set the map fragment as the default one
        onFragmentSelected(R.id.action_map);
    }

    public LiveData<HomeViewState> getViewState() {
        return viewState;
    }

    public LiveData<Void> getRequestLocationPermissionEvent() {
        return requestLocationPermissionEvent;
    }

    public void onLocationPermissionUpdated(boolean isPermissionGranted) {
        updateLocPermissionUseCase.updatePermission(isPermissionGranted);
    }

    public void onLocationUpdatesRemoved() {
        stopLocationUpdatesUseCase.stopUpdates();
    }

    public void onFragmentSelected(int menuItemId) {
        final HomeViewState oldViewState = viewState.getValue();

        // Set the fragment to hide as the old displayed fragment
        final String fragmentToHide = oldViewState != null
            ? oldViewState.getFragmentToShow()
            : null;

        // Set the fragment to show and the String ID for the toolbar title
        final String fragmentToShow;
        final int titleId;
        if (menuItemId == R.id.action_map) {
            fragmentToShow = TAG_FRAGMENT_MAP;
            titleId = R.string.title_restaurant;
        } else if (menuItemId == R.id.action_list) {
            fragmentToShow = TAG_FRAGMENT_RESTAURANT;
            titleId = R.string.title_restaurant;
        } else if (menuItemId == R.id.action_workmates) {
            fragmentToShow = TAG_FRAGMENT_WORKMATE;
            titleId = R.string.title_workmates;
        } else {
            throw new IllegalStateException("Unexpected value: " + menuItemId);
        }

        viewState.setValue(new HomeViewState(fragmentToShow, fragmentToHide, titleId));
    }
}
