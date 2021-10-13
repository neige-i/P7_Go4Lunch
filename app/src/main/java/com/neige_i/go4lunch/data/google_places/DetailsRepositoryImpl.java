package com.neige_i.go4lunch.data.google_places;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DetailsRepositoryImpl implements DetailsRepository {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final ExecutorService executorService;
    @NonNull
    private final Handler handler;
    @NonNull
    private final PlacesApi placesApi;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final LruCache<String, RestaurantDetails> detailsCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public DetailsRepositoryImpl(
        @NonNull ExecutorService executorService,
        @NonNull Handler handler,
        @NonNull PlacesApi placesApi
    ) {
        this.executorService = executorService;
        this.handler = handler;
        this.placesApi = placesApi;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<RestaurantDetails> getRestaurantDetails(@Nullable String placeId) {
        final MutableLiveData<RestaurantDetails> detailsRestaurantMutableLiveData = new MutableLiveData<>();

        if (placeId != null) {
            // Check if the request has already been executed
            final RestaurantDetails cachedRestaurantsDetails = detailsCache.get(placeId);
            if (cachedRestaurantsDetails != null) {
                Log.d("Neige", "REPO getDetailsRestaurant: from cache");
                detailsRestaurantMutableLiveData.setValue(cachedRestaurantsDetails);
            } else {
                executeAsync(placeId, detailsRestaurantMutableLiveData);
            }
        }

        return detailsRestaurantMutableLiveData;
    }

    // --------------------------------- BACKGROUND ASYNC METHODS ----------------------------------

    private void executeAsync(
        @NonNull String placeId,
        @NonNull MutableLiveData<RestaurantDetails> detailsRestaurantsMutableLiveData
    ) {
        executorService.execute(() -> {
            // Background thread
            final RestaurantDetails restaurantDetails = fetchDetailsRestaurantsInBackground(placeId);

            handler.post(() -> {
                // UI thread
                onBackgroundTaskComplete(restaurantDetails, detailsRestaurantsMutableLiveData, placeId);
            });
        });
    }

    @Nullable
    private RestaurantDetails fetchDetailsRestaurantsInBackground(@NonNull String placeId) {
        try {
            return getCleanDetailsRestaurants(
                placesApi.getRestaurantDetails(placeId).execute().body()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onBackgroundTaskComplete(
        @Nullable RestaurantDetails restaurantDetails,
        @NonNull MutableLiveData<RestaurantDetails> detailsRestaurantMutableLiveData,
        @NonNull String latLng
    ) {
        if (restaurantDetails != null) {
            Log.d("Neige", "REPO getDetailsResponse: from API");
            detailsRestaurantMutableLiveData.setValue(restaurantDetails);
            detailsCache.put(latLng, restaurantDetails);
        }
    }

    // --------------------------------- SETUP CLEAN POJO METHODS ----------------------------------

    @Nullable
    private RestaurantDetails getCleanDetailsRestaurants(@Nullable RawDetailsResponse rawDetailsResponse) {
        if (rawDetailsResponse != null && rawDetailsResponse.getResult() != null) {
            final RawDetailsResponse.Result result = rawDetailsResponse.getResult();
            if (result.getPlaceId() != null && result.getBusinessStatus() != null &&
                result.getBusinessStatus().equals("OPERATIONAL") &&
                result.getGeometry() != null && result.getGeometry().getLocation() != null &&
                result.getGeometry().getLocation().getLat() != null &&
                result.getGeometry().getLocation().getLng() != null &&
                result.getName() != null && result.getFormattedAddress() != null
            ) {
                return new RestaurantDetails(
                    result.getPlaceId(),
                    result.getName(),
                    result.getFormattedAddress(),
                    setupRating(result.getRating()),
                    setupPhoto(result.getPhotos()),
                    result.getInternationalPhoneNumber(),
                    result.getWebsite(),
                    setupOpeningHours(result.getOpeningHours())
                );
            }
        }

        return null;
    }

    @Nullable
    private List<String> setupOpeningHours(@Nullable RawDetailsResponse.OpeningHours openingHours) {
        if (openingHours == null || openingHours.getPeriods() == null) {
            return null;
        } else {
            final List<String> openingHourList = new ArrayList<>();

            for (RawDetailsResponse.Period period : openingHours.getPeriods()) {
                final RawDetailsResponse.Open open = period.getOpen();
                final RawDetailsResponse.Close close = period.getClose();

                if (open != null) {
                    if (open.getDay() != null && open.getTime() != null) {
                        openingHourList.add(toJavaDay(open.getDay()) + open.getTime());
                    } else {
                        return null;
                    }

                    if (close == null) { // When the place is always open
                        openingHourList.add(null);
                    } else if (close.getDay() != null && close.getTime() != null) {
                        openingHourList.add(toJavaDay(close.getDay()) + close.getTime());
                    } else {
                        return null;
                    }
                }
            }

            return openingHourList;
        }
    }

    /**
     * Converts a day from Places API standard to java.time standard.<br />
     * The day starts at monday Monday=1 for java.time and Sunday=0 for Places API;
     */
    private int toJavaDay(int placesApiDay) {
        return placesApiDay == 0 ? 7 : placesApiDay;
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
    private String setupPhoto(@Nullable List<RawDetailsResponse.Photo> photoList) {
        if (photoList == null || photoList.isEmpty() || photoList.get(0).getPhotoReference() == null) {
            return null;
        } else {
            return "https://maps.googleapis.com/" +
                "maps/api/place/photo?" +
                "maxheight=720" +
                "&key=" + BuildConfig.MAPS_API_KEY + // TODO: to inject
                "&photoreference=" + photoList.get(0).getPhotoReference();
        }
    }
}
