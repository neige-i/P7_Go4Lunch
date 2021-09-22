package com.neige_i.go4lunch.data.location;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LocationPermissionRepositoryImpl implements LocationPermissionRepository {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<Boolean> getLocationPermission() {
        return locationPermissionMutableLiveData;
    }

    @Override
    public void setLocationPermission(boolean locationPermission) {
        Log.d("Neige", "REPO setLocationPermission: " + locationPermission);
        locationPermissionMutableLiveData.setValue(locationPermission);
    }
}
