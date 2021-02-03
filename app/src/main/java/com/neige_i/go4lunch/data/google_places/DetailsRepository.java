package com.neige_i.go4lunch.data.google_places;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class DetailsRepository {

    @NonNull
    private final ExecutorService executorService;

    @NonNull
    private final LruCache<String, DetailsResponse> detailResponseCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    public DetailsRepository(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
    }

    public LiveData<DetailsResponse> executeDetailsRequest(@NonNull String placeId) {
        final MutableLiveData<DetailsResponse> detailsResponse = new MutableLiveData<>();

        // Check if the request has already been executed for this location
        final DetailsResponse cachedDetails = detailResponseCache.get(placeId);
        if (cachedDetails != null) {
            Log.d("Neige", "DetailsRepository::executeDetailRequest: cache");
            detailsResponse.setValue(cachedDetails);
        } else {
            Log.d("Neige", "DetailsRepository::executeDetailRequest: execute");
            // Use ExecutorService instead of AsyncTask because of its depreciation for background tasks
            executorService.execute(() -> {
                try {
                    // Fetch nearby restaurants from Google Places API asynchronously
                    final DetailsResponse newDetailsResponse = PlacesApi.getInstance()
                        .getRestaurantDetails(placeId)
                        .execute()
                        .body();

                    // Add NearbyResponse to cache and update LiveData (inside if because body() is @Nullable)
                    if (newDetailsResponse != null) {
                        detailResponseCache.put(placeId, newDetailsResponse);
                        detailsResponse.postValue(newDetailsResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return detailsResponse;
    }
}
