package com.neige_i.go4lunch.data.location;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class LocationPermissionRepositoryImpl implements LocationPermissionRepository {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<Boolean> getLocationPermission() {
        // The transformation below prevent requesting the permission in loop if the user denies it
        // TODO: remove distinct and put logic in ViewModel
        return Transformations.distinctUntilChanged(locationPermissionMutableLiveData);
    }

    @Override
    public void setLocationPermission(boolean newLocationPermission) {
        Log.d("Neige", "REPO setLocationPermission: " + newLocationPermission);
        locationPermissionMutableLiveData.setValue(newLocationPermission);
    }
}
