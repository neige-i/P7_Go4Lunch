package com.neige_i.go4lunch.domain.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetLocationUseCase {

    @NonNull
    LiveData<Location> get();
}
