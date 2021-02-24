package com.neige_i.go4lunch.data.google_places;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

public interface NearbyRepository {

    /**
     * Returns the nearby restaurants around the specified location.
     */
    @NonNull
    LiveData<NearbyResponse> getNearbyResponse(@Nullable Location location);
}
