package com.neige_i.go4lunch.data.google_places;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class PlacesRepository {

    @NonNull
    private final ExecutorService executorService;

    private final MutableLiveData<Boolean> isLocationPermissionGranted = new MutableLiveData<>();

    public PlacesRepository(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
    }

    public LiveData<NearbyResponse> getNearbyRestaurants() {
        final MutableLiveData<NearbyResponse> nearbyResponse = new MutableLiveData<>();

        // Use ExecutorService instead of AsyncTask because of its depreciation for background tasks
        executorService.execute(() -> {
            try {
                // Fetch nearby restaurants from Google Places API asynchronously, then update LiveData
                nearbyResponse.postValue(PlacesApi.getInstance().getNearbyRestaurants().execute().body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return nearbyResponse;
    }

    public LiveData<Boolean> isLocationPermissionGranted() {
        return isLocationPermissionGranted;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        isLocationPermissionGranted.setValue(locationPermissionGranted);
    }
}
