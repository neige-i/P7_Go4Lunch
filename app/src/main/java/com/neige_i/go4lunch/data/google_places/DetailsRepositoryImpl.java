package com.neige_i.go4lunch.data.google_places;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.MainApplication;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.DetailsRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class DetailsRepositoryImpl implements DetailsRepository {

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    private static final String NO_PHOTO = "no photo";

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final ExecutorService executorService;
    private final Handler handler;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final LruCache<String, DetailsRestaurant> detailsCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public DetailsRepositoryImpl(ExecutorService executorService, Handler handler) {
        this.executorService = executorService;
        this.handler = handler;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<DetailsRestaurant> getDetailsRestaurant(@Nullable String placeId) {
        final MutableLiveData<DetailsRestaurant> detailsRestaurantMutableLiveData = new MutableLiveData<>();

        if (placeId != null) {
            // Check if the request has already been executed
            final DetailsRestaurant cachedDetailsRestaurants = detailsCache.get(placeId);
            if (cachedDetailsRestaurants != null) {
                Log.d("Neige", "REPO getDetailsRestaurant: from cache");
                detailsRestaurantMutableLiveData.setValue(cachedDetailsRestaurants);
            } else {
                executeAsync(placeId, detailsRestaurantMutableLiveData);
            }
        }

        return detailsRestaurantMutableLiveData;
    }

    // --------------------------------- BACKGROUND ASYNC METHODS ----------------------------------

    private void executeAsync(@NonNull String placeId, @NonNull MutableLiveData<DetailsRestaurant> detailsRestaurantsMutableLiveData) {
        executorService.execute(() -> {
            // Background thread
            final DetailsRestaurant detailsRestaurant = fetchDetailsRestaurantsInBackground(placeId);

            handler.post(() -> {
                // UI thread
                onBackgroundTaskComplete(detailsRestaurant, detailsRestaurantsMutableLiveData, placeId);
            });
        });
    }

    @Nullable
    private DetailsRestaurant fetchDetailsRestaurantsInBackground(@NonNull String placeId) {
        try {
            return setupDetailsRestaurants(PlacesApi.getInstance().getRestaurantDetails(placeId).execute().body());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onBackgroundTaskComplete(@Nullable DetailsRestaurant detailsRestaurant,
                                          @NonNull MutableLiveData<DetailsRestaurant> detailsRestaurantMutableLiveData,
                                          @NonNull String latLng
    ) {
        if (detailsRestaurant != null) {
            Log.d("Neige", "REPO getDetailsResponse: from API");
            detailsRestaurantMutableLiveData.setValue(detailsRestaurant);
            detailsCache.put(latLng, detailsRestaurant);
        }
    }

    // --------------------------------- SETUP CLEAN POJO METHODS ----------------------------------

    @Nullable
    private DetailsRestaurant setupDetailsRestaurants(@Nullable RawDetailsResponse rawDetailsResponse) {
        if (rawDetailsResponse != null && rawDetailsResponse.getResult() != null) {
            final RawDetailsResponse.Result result = rawDetailsResponse.getResult();
            if (result.getPlaceId() != null && result.getBusinessStatus() != null &&
                result.getBusinessStatus().equals("OPERATIONAL") &&
                result.getGeometry() != null && result.getGeometry().getLocation() != null &&
                result.getGeometry().getLocation().getLat() != null &&
                result.getGeometry().getLocation().getLng() != null) {

                return new DetailsRestaurant(
                    result.getPlaceId(),
                    setupName(result.getName()),
                    setupAddress(result.getFormattedAddress()),
                    setupRating(result.getRating()),
                    setupPhoto(result.getPhotos()),
                    result.getInternationalPhoneNumber(),
                    result.getWebsite()
                );
            }
        }

        return null;
    }

    @NonNull
    private String setupName(@Nullable String restaurantName) {
        return restaurantName == null ?
            MainApplication.getInstance().getString(R.string.name_not_available) :
            restaurantName;
    }

    @NonNull
    private String setupAddress(@Nullable String restaurantAddress) {
        if (restaurantAddress == null) {
            return MainApplication.getInstance().getString(R.string.address_not_available);
        } else {
            final int commaIndex = restaurantAddress.indexOf(',');
            return commaIndex != -1 ?
                restaurantAddress.substring(0, commaIndex) :
                restaurantAddress;
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

    @NonNull
    private String setupPhoto(@Nullable List<RawDetailsResponse.Photo> photoList) {
        if (photoList == null || photoList.isEmpty() || photoList.get(0).getPhotoReference() == null) {
            return NO_PHOTO;
        } else {
            return "https://maps.googleapis.com/" +
                "maps/api/place/photo?" +
                "maxheight=720" +
                "&key=" + BuildConfig.MAPS_API_KEY +
                "&photoreference=" + photoList.get(0).getPhotoReference();
        }
    }
}
