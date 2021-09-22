package com.neige_i.go4lunch.data.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface LocationPermissionRepository {

    @NonNull
    LiveData<Boolean> getLocationPermission();

    void setLocationPermission(boolean locationPermission);
}
