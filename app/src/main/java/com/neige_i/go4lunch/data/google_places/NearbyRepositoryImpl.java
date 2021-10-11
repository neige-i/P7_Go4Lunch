package com.neige_i.go4lunch.data.google_places;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawNearbyResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NearbyRepositoryImpl implements NearbyRepository {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final ExecutorService executorService;
    @NonNull
    private final Handler handler;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final LruCache<String, List<NearbyRestaurant>> nearbyCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public NearbyRepositoryImpl(
        @NonNull ExecutorService executorService,
        @NonNull Handler handler
    ) {
        this.executorService = executorService;
        this.handler = handler;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<List<NearbyRestaurant>> getNearbyRestaurants(@Nullable Location location) {
        final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();

        if (location != null) {
            final String latLng = location.getLatitude() + "," + location.getLongitude();

            // Check if the request has already been executed
            final List<NearbyRestaurant> cachedNearbyRestaurants = nearbyCache.get(latLng);
            if (cachedNearbyRestaurants != null) {
                Log.d("Neige", "REPO getNearbyRestaurants: from cache");
                nearbyRestaurantsMutableLiveData.setValue(cachedNearbyRestaurants);
            } else {
                executeAsync(latLng, nearbyRestaurantsMutableLiveData);
            }
        }

        return nearbyRestaurantsMutableLiveData;
    }

    // --------------------------------- BACKGROUND ASYNC METHODS ----------------------------------

    private void executeAsync(
        @NonNull String latLng,
        @NonNull MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData
    ) {
        executorService.execute(() -> {
            // Background thread
            final List<NearbyRestaurant> nearbyRestaurants = fetchNearbyRestaurantsInBackground(latLng);

            handler.post(() -> {
                // UI thread
                onBackgroundTaskComplete(nearbyRestaurants, nearbyRestaurantsMutableLiveData, latLng);
            });
        });
    }

    @Nullable
    private List<NearbyRestaurant> fetchNearbyRestaurantsInBackground(@NonNull String latLng) {
        try {
            return setupNearbyRestaurants(
                // TODO: inject dependency
                PlacesApi.getInstance()
                    .getNearbyRestaurants(latLng)
                    .execute()
                    .body()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onBackgroundTaskComplete(
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @NonNull MutableLiveData<List<NearbyRestaurant>> nearbyResponseMutableLiveData,
        @NonNull String latLng
    ) {
        if (nearbyRestaurants != null) {
            Log.d("Neige", "REPO getNearbyRestaurants: from API");
            nearbyResponseMutableLiveData.setValue(nearbyRestaurants);
            nearbyCache.put(latLng, nearbyRestaurants);
        }
    }

    // --------------------------------- SETUP CLEAN POJO METHODS ----------------------------------

    @NonNull
    private List<NearbyRestaurant> setupNearbyRestaurants(@Nullable RawNearbyResponse rawNearbyResponse) {
        final List<NearbyRestaurant> nearbyRestaurants = new ArrayList<>();

        if (rawNearbyResponse != null && rawNearbyResponse.getResults() != null) {
            for (RawNearbyResponse.Result result : rawNearbyResponse.getResults()) {
                if (result.getPlaceId() != null && result.getBusinessStatus() != null &&
                    result.getBusinessStatus().equals("OPERATIONAL") &&
                    result.getGeometry() != null && result.getGeometry().getLocation() != null &&
                    result.getGeometry().getLocation().getLat() != null &&
                    result.getGeometry().getLocation().getLng() != null) {

                    nearbyRestaurants.add(new NearbyRestaurant(
                        result.getPlaceId(),
                        setupName(result.getName()),
                        setupAddress(result.getVicinity()),
                        result.getGeometry().getLocation().getLat(),
                        result.getGeometry().getLocation().getLng(),
                        setupRating(result.getRating()),
                        setupPhoto(result.getPhotos())
                    ));
                }
            }
        }

        return nearbyRestaurants;
    }

    @NonNull
    private String setupName(@Nullable String restaurantName) {
        // TODO: do not consider object if null
        return restaurantName == null ?
            "" :
            restaurantName;
    }

    @NonNull
    private String setupAddress(@Nullable String restaurantVicinity) {
        if (restaurantVicinity == null) {
            return "";
        } else {
            final int commaIndex = restaurantVicinity.indexOf(',');
            return commaIndex != -1 ?
                restaurantVicinity.substring(0, commaIndex) :
                restaurantVicinity;
        }
    }

    /**
     * Converts Google rating from 1.0 to 5.0 into Go4Lunch rating from 0 to 3
     * (or -1 if no rating is available).<br />
     * [1.0,5.0] -> (-1) -> [0.0,4.0] -> (*.75) -> [0.0,3.0] -> (round) -> [0,3]
     */
    private int setupRating(@Nullable Double rating) {
        return rating == null ? -1 : (int) Math.round((rating - 1) * .75);
    }

    @Nullable
    private String setupPhoto(@Nullable List<RawNearbyResponse.Photo> photoList) {
        if (photoList == null || photoList.isEmpty() || photoList.get(0).getPhotoReference() == null) {
            return null;
        } else {
            return "https://maps.googleapis.com/" +
                "maps/api/place/photo?" +
                "maxheight=720" +
                "&key=" + BuildConfig.MAPS_API_KEY +
                "&photoreference=" + photoList.get(0).getPhotoReference();
        }
    }
}
