package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.StopLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCase;
import com.neige_i.go4lunch.view.util.MediatorSingleLiveEvent;
import com.neige_i.go4lunch.view.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neige_i.go4lunch.view.home.HomeActivity.MAP_FRAGMENT_TAG;
import static com.neige_i.go4lunch.view.home.HomeActivity.RESTAURANT_FRAGMENT_TAG;
import static com.neige_i.go4lunch.view.home.HomeActivity.WORKMATE_FRAGMENT_TAG;

public class HomeViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final SetLocationPermissionUseCase setLocationPermissionUseCase;
    @NonNull
    private final StopLocationUpdatesUseCase stopLocationUpdatesUseCase;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MutableLiveData<Integer> titleIdState = new MutableLiveData<>();
    @NonNull
    private final MediatorSingleLiveEvent<Boolean> requestLocationPermissionEvent = new MediatorSingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<String> hideFragmentEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<String> showFragmentEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Boolean> addMapFragmentEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Boolean> addRestaurantFragmentEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Boolean> addWorkmateFragmentEvent = new SingleLiveEvent<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    /**
     * Control variable to prevent infinite loop when denying location permission.<br />
     * The location permission is checked inside onResume() and should be requested if it is not granted yet.
     * The problem happens if the user denies the permission: the permission dialog is naturally dismissed and the activity is resumed.
     * But, as previously stated, the permission is checked again while the activity is being resumed.
     * As the permission is not granted, it is requested again and here begins the infinite loop.
     */
    private boolean isLocationPermissionJustDenied;

    /**
     * List of tags representing the fragments that have already been displayed.<br />
     * The first tag, if present, represents the last displayed fragment.
     */
    private final List<String> currentFragmentTags = new ArrayList<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public HomeViewModel(@NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
                         @NonNull SetLocationPermissionUseCase setLocationPermissionUseCase,
                         @NonNull StopLocationUpdatesUseCase stopLocationUpdatesUseCase
    ) {
        this.setLocationPermissionUseCase = setLocationPermissionUseCase;
        this.stopLocationUpdatesUseCase = stopLocationUpdatesUseCase;

        handleLocationPermissionRequest(getLocationPermissionUseCase);

        // Set the default fragment to display
        onNavigationItemSelected(R.id.action_map);
    }

    @NonNull
    public LiveData<Integer> getTitleIdState() {
        return titleIdState;
    }

    @NonNull
    public MediatorSingleLiveEvent<Boolean> getRequestLocationPermissionEvent() {
        return requestLocationPermissionEvent;
    }

    @NonNull
    public LiveData<String> getHideFragmentEvent() {
        return hideFragmentEvent;
    }

    @NonNull
    public LiveData<String> getShowFragmentEvent() {
        return showFragmentEvent;
    }

    @NonNull
    public LiveData<Boolean> getAddMapFragmentEvent() {
        return addMapFragmentEvent;
    }

    @NonNull
    public LiveData<Boolean> getAddRestaurantFragmentEvent() {
        return addRestaurantFragmentEvent;
    }

    @NonNull
    public LiveData<Boolean> getAddWorkmateFragmentEvent() {
        return addWorkmateFragmentEvent;
    }

    // ------------------------------------- LOCATION METHODS --------------------------------------

    private void handleLocationPermissionRequest(@NonNull GetLocationPermissionUseCase getLocationPermissionUseCase) {
        requestLocationPermissionEvent.addSource(getLocationPermissionUseCase.isPermissionGranted(), isPermissionGranted -> {
            System.out.print("location permission has changed\t");
            if (isPermissionGranted) {
                System.out.println("if");
                isLocationPermissionJustDenied = false;
            } else if (!isLocationPermissionJustDenied) {
                System.out.println("else if");
                isLocationPermissionJustDenied = true;

                // Request the permission only if it is not currently granted and if the user has not just denied it
                requestLocationPermissionEvent.setValue(true);
            } else {
                System.out.println("else");
            }
        });
    }

    public void updateLocationPermission(boolean isPermissionGranted) {
        setLocationPermissionUseCase.setPermission(isPermissionGranted);
    }

    public void removeLocationUpdates() {
        stopLocationUpdatesUseCase.stopUpdates();
    }

    // ---------------------------------------- UI METHODS -----------------------------------------

    public void onNavigationItemSelected(int menuItemId) {
        updateToolbarTitle(menuItemId);
        updateDisplayedFragment(menuItemId);
    }

    private void updateToolbarTitle(int menuItemId) {
        final int titleId;

        if (menuItemId == R.id.action_map) {
            titleId = R.string.title_restaurant;
        } else if (menuItemId == R.id.action_restaurant) {
            titleId = R.string.title_restaurant;
        } else if (menuItemId == R.id.action_workmates) {
            titleId = R.string.title_workmates;
        } else {
            throw new IllegalStateException("Wrong MenuItem ID: " + menuItemId);
        }

        titleIdState.setValue(titleId);
    }

    private void updateDisplayedFragment(int menuItemId) {
        // 1. Hide the currently displayed fragment (if there is one)
        if (!currentFragmentTags.isEmpty()) {
            hideFragmentEvent.setValue(currentFragmentTags.get(0));
        }

        // 2. Set the tag of the fragment to display
        final String fragmentToDisplayTag;
        if (menuItemId == R.id.action_map) {
            fragmentToDisplayTag = MAP_FRAGMENT_TAG;
        } else if (menuItemId == R.id.action_restaurant) {
            fragmentToDisplayTag = RESTAURANT_FRAGMENT_TAG;
        } else if (menuItemId == R.id.action_workmates) {
            fragmentToDisplayTag = WORKMATE_FRAGMENT_TAG;
        } else {
            throw new IllegalStateException("Wrong MenuItem ID: " + menuItemId);
        }

        // 3. Show or add the fragment to display
        // Make sure to put the tag of the fragment to display as the first element of the list
        if (currentFragmentTags.contains(fragmentToDisplayTag)) {
            Collections.swap(currentFragmentTags, currentFragmentTags.indexOf(fragmentToDisplayTag), 0);
            showFragmentEvent.setValue(currentFragmentTags.get(0));
        } else {
            currentFragmentTags.add(0, fragmentToDisplayTag);
            switch (fragmentToDisplayTag) {
                case MAP_FRAGMENT_TAG:
                    addMapFragmentEvent.setValue(true);
                    break;
                case RESTAURANT_FRAGMENT_TAG:
                    addRestaurantFragmentEvent.setValue(true);
                    break;
                case WORKMATE_FRAGMENT_TAG:
                    addWorkmateFragmentEvent.setValue(true);
                    break;
            }
        }
    }
}
