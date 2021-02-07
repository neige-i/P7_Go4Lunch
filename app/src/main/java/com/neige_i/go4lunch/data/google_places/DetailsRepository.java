package com.neige_i.go4lunch.data.google_places;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.PlacesResponse;

import java.io.IOException;

public class DetailsRepository extends PlacesRepository {

    @Nullable
    @Override
    protected PlacesResponse executeRequest(@NonNull String arg) {
        Log.d("Neige", "DetailsRepository::executeRequest");
        try {
            return PlacesApi.getInstance()
                .getRestaurantDetails(arg)
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
        return o.toString();
    }
}
