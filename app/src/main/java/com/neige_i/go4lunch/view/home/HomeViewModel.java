package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.LocationRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;

import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_MAP;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_RESTAURANT;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_WORKMATE;

public class HomeViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;

    private final MutableLiveData<HomeUiModel> uiState = new MutableLiveData<>();
    private final MediatorLiveData<Void> requestLocationPermissionEvent = new MediatorLiveData<>();
    private String fragmentToHide = TAG_FRAGMENT_MAP;

    public HomeViewModel(@NonNull NearbyRepository nearbyRepository, @NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;

        requestLocationPermissionEvent.addSource(locationRepository.isLocationPermissionGranted(), isPermissionGranted -> {
            if (!isPermissionGranted)
                requestLocationPermissionEvent.setValue(null);
        });
        // TODO: change this addSource()
        requestLocationPermissionEvent.addSource(locationRepository.getCurrentLocation(), nearbyRepository::executeNearbyRestaurantsRequest);

        // Set the map fragment as the default one
        onFragmentSelected(R.id.action_map);
    }

    public LiveData<HomeUiModel> getUiState() {
        return uiState;
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

        uiState.setValue(new HomeUiModel(fragmentToShow, fragmentToHide, titleId));
    }

    public void setFragmentToHide(String fragmentToHide) {
        this.fragmentToHide = fragmentToHide;
    }
}
