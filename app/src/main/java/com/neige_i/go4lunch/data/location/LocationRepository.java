package com.neige_i.go4lunch.data.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.api.ResolvableApiException;

public interface LocationRepository {

    @NonNull
    LiveData<Location> getCurrentLocation();

    void startLocationUpdates();

    void removeLocationUpdates();

    boolean areLocationUpdatesNeverStarted();

    @NonNull
    LiveData<ResolvableApiException> getGpsDialog();

    void requestGpsDialog();
}
