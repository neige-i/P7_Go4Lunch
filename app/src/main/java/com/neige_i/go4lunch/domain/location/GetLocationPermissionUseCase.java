package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetLocationPermissionUseCase {

    @NonNull
    LiveData<Boolean> isPermissionGranted();
}
