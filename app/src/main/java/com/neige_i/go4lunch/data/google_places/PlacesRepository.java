package com.neige_i.go4lunch.data.google_places;

import android.os.AsyncTask;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.lang.ref.WeakReference;

public class PlacesRepository {

    @Nullable
    private NearbyRestaurantsAsyncTask asyncTask;

    @MainThread
    public LiveData<NearbyResponse> getNearbyRestaurants() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }

        final MutableLiveData<NearbyResponse> liveData = new MutableLiveData<>();

        // Start an async task to go on the internet and fetch data, asynchronously.
        // This line won't block : we return directly the LiveData that will, after a certain delay,
        // contain the result. But for now, it's an empty LiveData.
        asyncTask = new NearbyRestaurantsAsyncTask(new WeakReference<>(liveData));
        asyncTask.execute();

        return liveData;
    }

    private static class NearbyRestaurantsAsyncTask extends AsyncTask<Void, Void, NearbyResponse> {

        // Use a WeakReference to the LiveData to avoid a leak
        private final WeakReference<MutableLiveData<NearbyResponse>> liveDataWeakReference;

        public NearbyRestaurantsAsyncTask(WeakReference<MutableLiveData<NearbyResponse>> liveDataWeakReference) {
            this.liveDataWeakReference = liveDataWeakReference;
        }

        @Nullable
        @Override
        protected NearbyResponse doInBackground(Void... voids) {
            // This call can take some time to complete, that's why we use an AsyncTask.
            try {
                return PlacesApi.getInstance().getNearbyRestaurants().execute().body();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(@Nullable NearbyResponse githubRepos) {
            // If the view didn't die during the http request
            if (liveDataWeakReference.get() != null) {
                liveDataWeakReference.get().setValue(githubRepos);
            }
        }
    }
}
