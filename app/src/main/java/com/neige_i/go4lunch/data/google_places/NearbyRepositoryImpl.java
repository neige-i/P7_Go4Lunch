package com.neige_i.go4lunch.data.google_places;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.io.IOException;

public class NearbyRepositoryImpl implements NearbyRepository {

    @NonNull
    private final LruCache<String, NearbyResponse> nearbyCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    @NonNull
    @Override
    public LiveData<NearbyResponse> getNearbyResponse(@Nullable Location location) {
        final MutableLiveData<NearbyResponse> nearbyResponse = new MutableLiveData<>();

        if (location != null) {
            final String latLng = location.getLatitude() + "," + location.getLongitude();

            // Check if the request has already been executed
            final NearbyResponse cachedResponse = nearbyCache.get(latLng);
            if (cachedResponse != null) {
                Log.d("Neige", "PlacesRepository::getNearbyResponse: from cache");
                nearbyResponse.setValue(cachedResponse);
            } else {
                Log.d("Neige", "PlacesRepository::getNearbyResponse: need to execute request");
                new NearbyAsyncTask(nearbyResponse, nearbyCache, latLng).execute();
            }
        }

        return nearbyResponse;
    }

    private static class NearbyAsyncTask extends AsyncTask<Void, Void, NearbyResponse> {

        // Wrap objects inside WeakReference to avoid leaks
        @NonNull
        private final MutableLiveData<NearbyResponse> nearbyResponse;
        @NonNull
        private final LruCache<String, NearbyResponse> lruCache;
        @NonNull
        private final String latLng;

        @SuppressWarnings("deprecation")
        public NearbyAsyncTask(@NonNull MutableLiveData<NearbyResponse> nearbyResponse,
                               @NonNull LruCache<String, NearbyResponse> lruCache,
                               @NonNull String latLng
        ) {
            this.nearbyResponse = nearbyResponse;
            this.lruCache = lruCache;
            this.latLng = latLng;
        }

        @Nullable
        @Override
        protected NearbyResponse doInBackground(Void... voids) {
            Log.d("Neige", "NearbyRepository::executeRequest");
            try {
                return PlacesApi.getInstance()
                    .getNearbyRestaurants(latLng)
                    .execute()
                    .body();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable NearbyResponse result) {
            Log.d("Neige", "PlacesAsyncTask::onPostExecute");
            // If the view didn't die during the request and if the result of the AsyncTask is not null
            if (result != null) {
                // Add result to cache and update LiveData
                lruCache.put(latLng, result);
                nearbyResponse.setValue(result);
            }
        }
    }
}
