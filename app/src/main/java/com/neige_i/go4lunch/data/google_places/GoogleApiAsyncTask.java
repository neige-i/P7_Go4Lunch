package com.neige_i.go4lunch.data.google_places;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.BaseResponse;

import java.io.IOException;
import java.lang.ref.WeakReference;

public abstract class GoogleApiAsyncTask extends AsyncTask<Void, Void, BaseResponse> {

    // Wrap objects inside WeakReference to avoid leaks

    @NonNull
    private final WeakReference<MutableLiveData<BaseResponse>> liveDataWeakReference;
    @NonNull
    private final WeakReference<LruCache<String, BaseResponse>> cacheWeakReference;
    @NonNull
    private final String arg;

    @SuppressWarnings("deprecation" )
    public GoogleApiAsyncTask(@NonNull WeakReference<MutableLiveData<BaseResponse>> liveDataWeakReference, @NonNull WeakReference<LruCache<String, BaseResponse>> cacheWeakReference, @NonNull String arg) {
        this.liveDataWeakReference = liveDataWeakReference;
        this.cacheWeakReference = cacheWeakReference;
        this.arg = arg;
    }

    @Nullable
    @Override
    protected BaseResponse doInBackground(Void... voids) {
        try {
            return executeRequest(arg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    protected abstract BaseResponse executeRequest(@NonNull String arg) throws IOException;
}