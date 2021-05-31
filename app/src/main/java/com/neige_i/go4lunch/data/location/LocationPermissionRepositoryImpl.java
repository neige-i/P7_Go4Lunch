package com.neige_i.go4lunch.data.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LocationPermissionRepositoryImpl implements LocationPermissionRepository {

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<Boolean> getLocationPermission() {
        return locationPermission;
    }

    @Override
    public void updateLocationPermission(boolean isPermissionGranted) {
        locationPermission.setValue(isPermissionGranted);
    }
}
