package com.neige_i.go4lunch.repository.location;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;

public class LocationPermissionRepositoryImpl implements LocationPermissionRepository {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final Application application;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public LocationPermissionRepositoryImpl(@NonNull Application application) {
        this.application = application;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @Override
    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
