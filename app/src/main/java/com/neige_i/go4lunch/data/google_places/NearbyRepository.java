package com.neige_i.go4lunch.data.google_places;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.PlacesResponse;

import java.io.IOException;

public class NearbyRepository extends PlacesRepository {

    @Nullable
    @Override
    protected PlacesResponse executeRequest(@NonNull String arg) {
        Log.d("Neige", "NearbyRepository::executeRequest");
        try {
            return PlacesApi.getInstance()
                .getNearbyRestaurants(arg)
                .execute()
                .body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    @Override
    protected String argToString(@NonNull Object o) {
        final Location location = (Location) o;
        return location.getLatitude() + "," + location.getLongitude();
    }
}
