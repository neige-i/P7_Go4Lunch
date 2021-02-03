package com.neige_i.go4lunch.data.google_places;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;
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

    @NonNull
    private final LruCache<String, NearbyResponse> nearbyResponseCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    public NearbyRepository(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
    }

    public LiveData<NearbyResponse> getNearbyRestaurants() {
        return nearbyResponse;
    }

    public void executeNearbyRestaurantsRequest(@NonNull Location newLocation) {
        final String stringLocation = newLocation.getLatitude() + "," + newLocation.getLongitude();

        // Check if the request has already been executed for this location
        final NearbyResponse cachedNearby = nearbyResponseCache.get(stringLocation);
        if (cachedNearby != null) {
            // TODO: use cache also if new location is close enough (say 100m)
            nearbyResponse.setValue(cachedNearby);
        } else {
            // Use ExecutorService instead of AsyncTask because of its depreciation for background tasks
            executorService.execute(() -> {
                try {
                    // Fetch nearby restaurants from Google Places API asynchronously
                    final NearbyResponse newNearbyResponse = PlacesApi.getInstance()
                        .getNearbyRestaurants(stringLocation)
                        .execute()
                        .body();

                    // Add NearbyResponse to cache and update LiveData
                    if (newNearbyResponse != null) {
                        nearbyResponseCache.put(stringLocation, newNearbyResponse);
                        nearbyResponse.postValue(newNearbyResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
