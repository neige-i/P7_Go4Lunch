package com.neige_i.go4lunch.data.google_places;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;

import java.io.IOException;

public class DetailsRepositoryImpl implements DetailsRepository {

    @NonNull
    private final LruCache<String, DetailsResponse> detailsCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    @NonNull
    @Override
    public LiveData<DetailsResponse> getDetailsResponse(@Nullable String placeId) {
        final MutableLiveData<DetailsResponse> detailsResponse = new MutableLiveData<>();

        if (placeId != null) {
            // Check if the request has already been executed
            final DetailsResponse cachedResponse = detailsCache.get(placeId);
            if (cachedResponse != null) {
                Log.d("Neige", "PlacesRepository::getPlacesResponse: from cache");
                detailsResponse.setValue(cachedResponse);
            } else {
                Log.d("Neige", "PlacesRepository::getPlacesResponse: need to execute request");
                new DetailsAsyncTask(detailsResponse, detailsCache, placeId).execute();
            }
        }

        return detailsResponse;
    }

    private static class DetailsAsyncTask extends AsyncTask<Void, Void, DetailsResponse> {

        // Wrap objects inside WeakReference to avoid leaks
        @NonNull
        private final MutableLiveData<DetailsResponse> detailsResponse;
        @NonNull
        private final LruCache<String, DetailsResponse> lruCache;
        @NonNull
        private final String placeId;

        @SuppressWarnings("deprecation")
        public DetailsAsyncTask(@NonNull MutableLiveData<DetailsResponse> detailsResponse,
                                @NonNull LruCache<String, DetailsResponse> lruCache,
                                @NonNull String placeId
        ) {
            this.detailsResponse = detailsResponse;
            this.lruCache = lruCache;
            this.placeId = placeId;
        }

        @Nullable
        @Override
        protected DetailsResponse doInBackground(Void... voids) {
            try {
                return PlacesApi.getInstance()
                    .getRestaurantDetails(placeId)
                    .execute()
                    .body();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable DetailsResponse result) {
            Log.d("Neige", "PlacesAsyncTask::onPostExecute");
            // If the view didn't die during the request and if the result of the AsyncTask is not null
            if (result != null) {
                // Add result to cache and update LiveData
                lruCache.put(placeId, result);
                detailsResponse.setValue(result);
            }
        }
    }
}
