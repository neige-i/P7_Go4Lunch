package com.neige_i.go4lunch.data.google_places;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.PlacesResponse;

import java.lang.ref.WeakReference;

public abstract class PlacesRepository {

    @Nullable
    private PlacesAsyncTask placesAsyncTask;

    @NonNull
    private final LruCache<String, PlacesResponse> placesResponseCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    @NonNull
    public LiveData<PlacesResponse> getPlacesResponse(@NonNull Object requestArg) {
        final MutableLiveData<PlacesResponse> placesResponse = new MutableLiveData<>();

        final String arg = argToString(requestArg);

        // Check if the request has already been executed
        final PlacesResponse cachedResponse = placesResponseCache.get(arg);
        if (cachedResponse != null) {
            Log.d("Neige", "PlacesRepository::getPlacesResponse: from cache");
            placesResponse.setValue(cachedResponse);
        } else {
            Log.d("Neige", "PlacesRepository::getPlacesResponse: need to execute request");
            // Check if the AsyncTask is already running and cancel it if so
            if (placesAsyncTask != null) {
                Log.d("Neige", "PlacesRepository::getPlacesResponse: cancel current AsyncTask");
                placesAsyncTask.cancel(true);
                placesAsyncTask = null; // ASKME: necessary, new instance just below
            }

            //noinspection unchecked,rawtypes
            placesAsyncTask = new PlacesAsyncTask(
                new WeakReference(placesResponse),
                new WeakReference(placesResponseCache),
                arg,
                this::executeRequest
            );
            placesAsyncTask.execute();
        }

        return placesResponse;
    }

    @Nullable
    protected abstract PlacesResponse executeRequest(@NonNull String arg);

    @NonNull
    protected abstract String argToString(@NonNull Object o);

    private static class PlacesAsyncTask extends AsyncTask<Void, Void, PlacesResponse> {

        // Wrap objects inside WeakReference to avoid leaks
        @NonNull
        private final WeakReference<MutableLiveData<PlacesResponse>> liveDataWeakReference;
        @NonNull
        private final WeakReference<LruCache<String, PlacesResponse>> cacheWeakReference;
        @NonNull
        private final String arg;
        @NonNull
        private final PlacesAsyncTaskCallback placesAsyncTaskCallback;

        @SuppressWarnings("deprecation")
        public PlacesAsyncTask(@NonNull WeakReference<MutableLiveData<PlacesResponse>> liveDataWeakReference, @NonNull WeakReference<LruCache<String, PlacesResponse>> cacheWeakReference, @NonNull String arg, @NonNull PlacesAsyncTaskCallback placesAsyncTaskCallback) {
            this.liveDataWeakReference = liveDataWeakReference;
            this.cacheWeakReference = cacheWeakReference;
            this.arg = arg;
            this.placesAsyncTaskCallback = placesAsyncTaskCallback;
        }

        @Nullable
        @Override
        protected PlacesResponse doInBackground(Void... voids) {
            return placesAsyncTaskCallback.onExecute(arg);
        }

        @Override
        protected void onPostExecute(@Nullable PlacesResponse result) {
            Log.d("Neige", "PlacesAsyncTask::onPostExecute");
            // If the view didn't die during the request and if the result of the AsyncTask is not null
            if (liveDataWeakReference.get() != null && cacheWeakReference.get() != null && result != null) {
                // Add result to cache and update LiveData
                cacheWeakReference.get().put(arg, result);
                liveDataWeakReference.get().setValue(result);
            }
        }

    }

    interface PlacesAsyncTaskCallback {
        @Nullable
        PlacesResponse onExecute(String arg);
    }
}
