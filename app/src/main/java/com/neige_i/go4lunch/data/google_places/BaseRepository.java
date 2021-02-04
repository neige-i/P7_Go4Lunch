package com.neige_i.go4lunch.data.google_places;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.BaseResponse;

import java.lang.ref.WeakReference;

public abstract class BaseRepository {

    @Nullable
    private GoogleApiAsyncTask asyncTask;

    @NonNull
    private final LruCache<String, BaseResponse> apiResponseCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    public LiveData<BaseResponse> executeDetailsRequest(@NonNull Object placeId) {
        final MutableLiveData<BaseResponse> baseResponse = new MutableLiveData<>();

        final String arg = getArg(placeId);

        // Check if the request has already been executed for this location
        final BaseResponse cachedResponse = apiResponseCache.get(arg);
        if (cachedResponse != null) {
            Log.d("Neige", "DetailsRepository::executeDetailRequest: cache");
            baseResponse.setValue(cachedResponse);
        } else {
            Log.d("Neige", "DetailsRepository::executeDetailRequest: execute");
            // Check if the AsyncTask is already running and cancel it if so
            if (asyncTask != null) {
                asyncTask.cancel(true);
                asyncTask = null; // ASKME: necessary, new instance just below
            }

            //noinspection unchecked,rawtypes
            asyncTask = new GoogleApiAsyncTask(
                new WeakReference(baseResponse),
                new WeakReference(apiResponseCache),
                arg,
                this::executeRequest
            );
            asyncTask.execute();
        }

        return baseResponse;
    }

    protected abstract BaseResponse executeRequest(@NonNull String arg);
    protected abstract String getArg(Object o);

    private static class GoogleApiAsyncTask extends AsyncTask<Void, Void, BaseResponse> {

        // Wrap objects inside WeakReference to avoid leaks

        @NonNull
        private final WeakReference<MutableLiveData<BaseResponse>> liveDataWeakReference;
        @NonNull
        private final WeakReference<LruCache<String, BaseResponse>> cacheWeakReference;
        @NonNull
        private final String arg;
        private final AsyncTaskCallback asyncTaskCallback;

        @SuppressWarnings("deprecation" )
        public GoogleApiAsyncTask(@NonNull WeakReference<MutableLiveData<BaseResponse>> liveDataWeakReference, @NonNull WeakReference<LruCache<String, BaseResponse>> cacheWeakReference, @NonNull String arg, AsyncTaskCallback asyncTaskCallback) {
            this.liveDataWeakReference = liveDataWeakReference;
            this.cacheWeakReference = cacheWeakReference;
            this.arg = arg;
            this.asyncTaskCallback = asyncTaskCallback;
        }

        @Nullable
        @Override
        protected BaseResponse doInBackground(Void... voids) {
            return asyncTaskCallback.onExecute(arg);
        }

        @Override
        protected void onPostExecute(@Nullable BaseResponse result) {
            // If the view didn't die during the request and if the result of the AsyncTask is not null
            if (liveDataWeakReference.get() != null && cacheWeakReference.get() != null && result != null) {
                // Add result to cache and update LiveData
                cacheWeakReference.get().put(arg, result);
                liveDataWeakReference.get().setValue(result);
            }
        }

    }

    interface AsyncTaskCallback {
        BaseResponse onExecute(String arg);
    }
}
