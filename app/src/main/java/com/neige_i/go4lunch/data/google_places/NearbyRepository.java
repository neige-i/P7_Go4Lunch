package com.neige_i.go4lunch.data.google_places;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;

import java.util.List;

public interface NearbyRepository {

    /**
     * Returns the nearby restaurants around the specified location.
     */
    @NonNull
    LiveData<List<NearbyRestaurant>> getNearbyRestaurants(@Nullable Location location);
}
