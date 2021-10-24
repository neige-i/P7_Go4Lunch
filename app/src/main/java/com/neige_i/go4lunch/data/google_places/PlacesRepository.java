package com.neige_i.go4lunch.data.google_places;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract class PlacesRepository<QueryParameter, RawData, CleanResponse> {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final String mapsApiKey;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final LruCache<String, CleanResponse> dataCache = new LruCache<>(4 * 1024 * 1024); // 4 MB cache size

    PlacesRepository(@NonNull String mapsApiKey) {
        this.mapsApiKey = mapsApiKey;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    public LiveData<CleanResponse> getData(@Nullable QueryParameter queryParameter) {
        if (queryParameter == null) {
            return new MutableLiveData<>();
        }

        final MutableLiveData<CleanResponse> responseMutableLiveData = new MutableLiveData<>();

        // Converts the query parameter into a String usable in a HTML request
        final String queryString = toQueryString(queryParameter);

        final CleanResponse cachedCleanResponse = dataCache.get(queryString);

        // Check if the request has already been executed
        if (cachedCleanResponse != null) {
            Log.d("Neige", "REPO get " + getNameForLog() + " : from cache");
            responseMutableLiveData.setValue(cachedCleanResponse);
        } else {
            getRequest(queryString).enqueue(new Callback<RawData>() {
                @Override
                public void onResponse(
                    @NonNull Call<RawData> call,
                    @NonNull Response<RawData> response
                ) {
                    final CleanResponse cleanResponse = cleanDataFromRetrofit(response.body());

                    if (cleanResponse != null) {
                        Log.d("Neige", "REPO get " + getNameForLog() + " : from API");
                        responseMutableLiveData.setValue(cleanResponse);
                        dataCache.put(queryString, cleanResponse);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RawData> call, @NonNull Throwable t) {
                }
            });
        }

        return responseMutableLiveData;
    }

    @NonNull
    abstract String toQueryString(@NonNull QueryParameter queryParameter);

    @NonNull
    abstract Call<RawData> getRequest(@NonNull String queryParameter);

    @NonNull
    abstract String getNameForLog();

    // ---------------------------------- CLEAN RESPONSE METHODS -----------------------------------

    @Nullable
    abstract CleanResponse cleanDataFromRetrofit(@Nullable RawData rawData);

    /**
     * Converts Google rating from 1.0 to 5.0 into Go4Lunch rating from 0 to 3
     * (or -1 if no rating is available).<br />
     * [1.0,5.0] -> (-1) -> [0.0,4.0] -> (*.75) -> [0.0,3.0] -> (round) -> [0,3]
     */
    int getRating(@Nullable Double rating) {
        return rating == null ? -1 : (int) Math.round((rating - 1) * .75);
    }

    @Nullable
    String getPhotoUrl(@Nullable String photoReference) {
        if (photoReference != null) {
            return "https://maps.googleapis.com/" +
                "maps/api/place/photo?" +
                "maxheight=720" +
                "&key=" + mapsApiKey +
                "&photoreference=" + photoReference;
        } else {
            return null;
        }
    }
}
