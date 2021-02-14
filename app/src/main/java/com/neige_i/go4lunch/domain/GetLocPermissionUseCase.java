package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetLocPermissionUseCase {

    @NonNull
    LiveData<Boolean> isPermissionGranted();
}
