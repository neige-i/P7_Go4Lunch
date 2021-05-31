package com.neige_i.go4lunch.data.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface LocationRepository {

    @NonNull
    LiveData<Location> getCurrentLocation();

    void startLocationUpdates();

    void removeLocationUpdates();
}
