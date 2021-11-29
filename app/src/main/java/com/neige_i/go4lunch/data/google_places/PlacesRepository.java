package com.neige_i.go4lunch.data.google_places;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract class PlacesRepository<RawData, CleanResponse> {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    final PlacesApi placesApi;
    @NonNull
    private final String mapsApiKey;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final LruCache<List<String>, CleanResponse> dataCache = new LruCache<>(4 * 1024 * 1024); // 4 MB cache size

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    PlacesRepository(
        @NonNull PlacesApi placesApi,
        @NonNull String mapsApiKey
    ) {
        this.placesApi = placesApi;
        this.mapsApiKey = mapsApiKey;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    public LiveData<CleanResponse> getData(@Nullable Object... queryParameter) {
        if (queryParameter == null) {
            return new MutableLiveData<>();
        }

        final MutableLiveData<CleanResponse> responseMutableLiveData = new MutableLiveData<>();

        // Converts the query parameter into a String usable in a HTML request
        final List<String> queryStrings = toQueryStrings(queryParameter);

        final CleanResponse cachedCleanResponse = dataCache.get(queryStrings);

        // Check if the request has already been executed
        if (cachedCleanResponse != null) {
            Log.d("Neige", "REPO get " + getNameForLog() + " : from cache");
            responseMutableLiveData.setValue(cachedCleanResponse);
        } else {
            getRequest(queryStrings).enqueue(new Callback<RawData>() {
                @Override
                public void onResponse(
                    @NonNull Call<RawData> call,
                    @NonNull Response<RawData> response
                ) {
                    final CleanResponse cleanResponse = cleanDataFromRetrofit(response.body());

                    if (cleanResponse != null) {
                        Log.d("Neige", "REPO get " + getNameForLog() + " : from API");
                        responseMutableLiveData.setValue(cleanResponse);
                        dataCache.put(queryStrings, cleanResponse);
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
    abstract List<String> toQueryStrings(@NonNull Object... queryParameter);

    @NonNull
    abstract Call<RawData> getRequest(@NonNull List<String> queryParameters);

    @NonNull
    abstract String getNameForLog();

    // ---------------------------------- CLEAN RESPONSE METHODS -----------------------------------

    @Nullable
    abstract CleanResponse cleanDataFromRetrofit(@Nullable RawData rawData);

    @NonNull
    String getLocationString(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    @NonNull
    String getAddress(@NonNull String completeAddress) {
        int commaIndex = completeAddress.indexOf(",");
        return commaIndex != -1 ? completeAddress.substring(0, commaIndex) : completeAddress;
    }

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
        if (photoReference == null) {
            return null;
        }

        return "https://maps.googleapis.com/" +
            "maps/api/place/photo?" +
            "maxheight=720" +
            "&key=" + mapsApiKey +
            "&photoreference=" + photoReference;
    }
}
