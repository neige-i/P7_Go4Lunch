package com.neige_i.go4lunch.data.google_places;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.google_places.model.BaseResponse;

import java.io.IOException;

public class DetailsRepository extends BaseRepository {
    @Override
    protected BaseResponse executeRequest(@NonNull String arg) {
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

    @Override
    protected String getArg(Object o) {
        return (String) o;
    }
}
