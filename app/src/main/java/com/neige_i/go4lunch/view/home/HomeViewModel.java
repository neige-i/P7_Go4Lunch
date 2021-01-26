package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.PlacesRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_MAP;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_RESTAURANT;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_WORKMATE;

public class HomeViewModel extends ViewModel {

    @NonNull
    private final PlacesRepository placesRepository;

    private final MutableLiveData<HomeUiModel> uiState = new MutableLiveData<>();
    private String fragmentToHide = TAG_FRAGMENT_MAP;

    public HomeViewModel(@NonNull PlacesRepository placesRepository) {
        this.placesRepository = placesRepository;

        // Set the map fragment as the default one
        onFragmentSelected(R.id.action_map);
    }

    public LiveData<HomeUiModel> getUiState() {
        return uiState;
    }

    public LiveData<NearbyResponse> getNearbyRestaurants() {
        return placesRepository.getNearbyRestaurants();
    }

    public void onLocationPermissionGranted(boolean isLocationPermissionGranted) {
        // Update repository value
        placesRepository.setLocationPermissionGranted(isLocationPermissionGranted);
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
