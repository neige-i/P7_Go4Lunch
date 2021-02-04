package com.neige_i.go4lunch.data.google_places;

import android.location.Location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.google_places.model.BaseResponse;

import java.io.IOException;

public class NearbyRepository extends BaseRepository {
    @Override
    protected BaseResponse executeRequest(@NonNull String arg) {
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

    @Override
    protected String getArg(Object o) {
        final Location location = (Location) o;
        return location.getLatitude() + "," + location.getLongitude();
    }
}
