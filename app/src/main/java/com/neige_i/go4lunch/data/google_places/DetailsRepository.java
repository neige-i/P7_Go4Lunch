package com.neige_i.go4lunch.data.google_places;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.BaseResponse;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class DetailsRepository {

    @Nullable
    private GoogleApiAsyncTask detailsAsyncTask;

    @NonNull
    private final LruCache<String, DetailsResponse> detailsResponseCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    public LiveData<DetailsResponse> executeDetailsRequest(@NonNull String placeId) {
        final MutableLiveData<DetailsResponse> detailsResponse = new MutableLiveData<>();

        // Check if the request has already been executed for this location
        final DetailsResponse cachedDetails = detailsResponseCache.get(placeId);
        if (cachedDetails != null) {
            Log.d("Neige", "DetailsRepository::executeDetailRequest: cache");
            detailsResponse.setValue(cachedDetails);
        } else {
            Log.d("Neige", "DetailsRepository::executeDetailRequest: execute");
            // Check if the AsyncTask is already running and cancel it if so
            if (detailsAsyncTask != null) {
                detailsAsyncTask.cancel(true);
                detailsAsyncTask = null; // ASKME: necessary, new instance just below
            }

            //noinspection unchecked,rawtypes
            detailsAsyncTask = new DetailsAsyncTask(new WeakReference(detailsResponse), new WeakReference(detailsResponseCache), placeId);
            detailsAsyncTask.execute();
        }

        return detailsResponse;
    }

    private static class DetailsAsyncTask extends GoogleApiAsyncTask {

        public DetailsAsyncTask(@NonNull WeakReference<MutableLiveData<BaseResponse>> liveDataWeakReference, @NonNull WeakReference<LruCache<String, BaseResponse>> cacheWeakReference, @NonNull String arg) {
            super(liveDataWeakReference, cacheWeakReference, arg);
        }

        @Override
        protected DetailsResponse executeRequest(@NonNull String arg) throws IOException {
            return PlacesApi.getInstance()
                .getRestaurantDetails(arg)
                .execute()
                .body();
        }
    }
}
