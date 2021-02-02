package com.neige_i.go4lunch.data.google_places;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class NearbyRepository {

    @NonNull
    private final ExecutorService executorService;

    @NonNull
    private final MutableLiveData<NearbyResponse> nearbyResponse = new MutableLiveData<>();

    public NearbyRepository(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
    }

    public LiveData<NearbyResponse> getNearbyRestaurants() {
        return nearbyResponse;
    }

    public void executeNearbyRestaurantsRequest(@NonNull Location newLocation) {
        // Use ExecutorService instead of AsyncTask because of its depreciation for background tasks
        executorService.execute(() -> {
            try {
                // Fetch nearby restaurants from Google Places API asynchronously, then update LiveData
                nearbyResponse.postValue(PlacesApi.getInstance().getNearbyRestaurants(
                    newLocation.getLatitude() + "," + newLocation.getLongitude()
                ).execute().body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
