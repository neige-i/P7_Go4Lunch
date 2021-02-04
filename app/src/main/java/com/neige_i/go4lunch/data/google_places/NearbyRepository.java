package com.neige_i.go4lunch.data.google_places;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.BaseResponse;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class NearbyRepository {

    @Nullable
    private GoogleApiAsyncTask nearbyAsyncTask;

    @NonNull
    private final MutableLiveData<NearbyResponse> nearbyResponse = new MutableLiveData<>();

    @NonNull
    private final LruCache<String, NearbyResponse> nearbyResponseCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    public LiveData<NearbyResponse> getNearbyRestaurants() {
        return nearbyResponse;
    }

    public void executeNearbyRestaurantsRequest(@NonNull Location newLocation) {
        final String latLng = newLocation.getLatitude() + "," + newLocation.getLongitude();

        // Check if the request has already been executed for this location
        final NearbyResponse cachedNearby = (NearbyResponse) nearbyResponseCache.get(latLng);
        if (cachedNearby != null) {
            Log.d("Neige", "NearbyRepository::executeNearbyRestaurantsRequest: execute");
            // TODO: use cache also if new location is close enough (say 100m)
            nearbyResponse.setValue(cachedNearby);
        } else {
            Log.d("Neige", "NearbyRepository::executeNearbyRestaurantsRequest: request");
            // Check if the AsyncTask is already running and cancel it if so
            if (nearbyAsyncTask != null) {
                nearbyAsyncTask.cancel(true);
                nearbyAsyncTask = null; // ASKME: necessary, new instance just below
            }

            //noinspection unchecked,rawtypes
            nearbyAsyncTask = new NearbyAsyncTask(new WeakReference(nearbyResponse), new WeakReference(nearbyResponseCache), latLng);
            nearbyAsyncTask.execute();
        }
    }

    private static class NearbyAsyncTask extends GoogleApiAsyncTask {

        public NearbyAsyncTask(@NonNull WeakReference<MutableLiveData<BaseResponse>> liveDataWeakReference, @NonNull WeakReference<LruCache<String, BaseResponse>> cacheWeakReference, @NonNull String arg) {
            super(liveDataWeakReference, cacheWeakReference, arg);
        }

        @Override
        protected NearbyResponse executeRequest(@NonNull String arg) throws IOException {
            return PlacesApi.getInstance()
                .getNearbyRestaurants(arg)
                .execute()
                .body();
        }
    }
}
