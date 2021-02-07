package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.LocationRepository;

import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_MAP;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_RESTAURANT;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_WORKMATE;

public class HomeViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;

    private final MutableLiveData<HomeViewState> viewState = new MutableLiveData<>();
    private final MediatorLiveData<Void> requestLocationPermissionEvent = new MediatorLiveData<>();
    private boolean isLocationPermissionRequested;

    public HomeViewModel(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;

        // ASKME: replace the MediatorLiveData by SingleLiveEvent and put a condition in HomeActivity
        requestLocationPermissionEvent.addSource(locationRepository.isLocationPermissionGranted(), isPermissionGranted -> {
            if (isPermissionGranted) {
                isLocationPermissionRequested = false;
            } else if (!isLocationPermissionRequested) {
                isLocationPermissionRequested = true;
                requestLocationPermissionEvent.setValue(null);
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

    public void onLocationPermissionGranted(boolean isLocationPermissionGranted) {
        locationRepository.setLocationPermissionGranted(isLocationPermissionGranted);
    }

    public void onLocationPermissionChecked() {
        locationRepository.checkLocationPermission();
    }

    public void onLocationUpdatesRemoved() {
        locationRepository.removeLocationUpdates();
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
