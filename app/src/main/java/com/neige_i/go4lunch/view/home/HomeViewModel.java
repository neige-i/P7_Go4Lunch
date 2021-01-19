package com.neige_i.go4lunch.view.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;

import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_MAP;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_RESTAURANT;
import static com.neige_i.go4lunch.view.home.HomeActivity.TAG_FRAGMENT_WORKMATE;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<HomeUiModel> uiState = new MutableLiveData<>();
    private String fragmentToHide = TAG_FRAGMENT_MAP;

    public HomeViewModel() {
        // Set the map fragment as the default one
        onFragmentSelected(R.id.action_map);
    }

    public LiveData<HomeUiModel> getUiState() {
        return uiState;
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
