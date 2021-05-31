package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.location.GetGpsResolvableUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;

public class HomeViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final SetLocationPermissionUseCase setLocationPermissionUseCase;
    @NonNull
    private final SetLocationUpdatesUseCase setLocationUpdatesUseCase;
    @NonNull
    private final GetGpsResolvableUseCase getGpsResolvableUseCase;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<HomeViewState> homeViewState = new MutableLiveData<>();
    @NonNull
    private final MediatorSingleLiveEvent<Void> requestLocationPermissionEvent = new MediatorSingleLiveEvent<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public HomeViewModel(@NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
                         @NonNull SetLocationPermissionUseCase setLocationPermissionUseCase,
                         @NonNull SetLocationUpdatesUseCase setLocationUpdatesUseCase,
                         @NonNull GetGpsResolvableUseCase getGpsResolvableUseCase
    ) {
        this.setLocationPermissionUseCase = setLocationPermissionUseCase;
        this.setLocationUpdatesUseCase = setLocationUpdatesUseCase;
        this.getGpsResolvableUseCase = getGpsResolvableUseCase;

        // Request the permission if it is not granted
        requestLocationPermissionEvent.addSource(getLocationPermissionUseCase.isGranted(), isPermissionGranted -> {
            if (!isPermissionGranted) {
                requestLocationPermissionEvent.call();
            }
        });

        // Init the default view state
        setViewState(R.id.action_map);
    }

    @NonNull
    public LiveData<HomeViewState> getHomeViewState() {
        return homeViewState;
    }

    @NonNull
    public LiveData<Void> getRequestLocationPermissionEvent() {
        return requestLocationPermissionEvent;
    }

    // ------------------------------------- LOCATION METHODS --------------------------------------

    public void setLocationPermissionAndUpdates(boolean locationPermission) {
        setLocationPermissionUseCase.set(locationPermission);
        setLocationUpdatesUseCase.set(locationPermission);
    }

    public void stopLocationUpdates() {
        setLocationUpdatesUseCase.set(false);
    }

    @NonNull
    public LiveData<ResolvableApiException> getEnableGpsEvent() {
        return getGpsResolvableUseCase.getResolvable();
    }

    // ---------------------------------------- UI METHODS -----------------------------------------

    public void setViewState(int menuItemId) {
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
            throw new IllegalArgumentException("setViewState() was called with a wrong MenuItem ID: " + menuItemId);
        }

        homeViewState.setValue(new HomeViewState(titleId, viewPagerPosition));
    }
}
