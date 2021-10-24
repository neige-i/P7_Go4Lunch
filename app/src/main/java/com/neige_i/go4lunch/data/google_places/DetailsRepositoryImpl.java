package com.neige_i.go4lunch.data.google_places;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.data.google_places.model.RestaurantHour;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class DetailsRepositoryImpl implements DetailsRepository {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final PlacesApi placesApi;
    @NonNull
    private final CleanRestaurantDelegate cleanRestaurantDelegate;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final LruCache<String, RestaurantDetails> detailsCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public DetailsRepositoryImpl(
        @NonNull PlacesApi placesApi,
        @NonNull CleanRestaurantDelegate cleanRestaurantDelegate
    ) {
        this.placesApi = placesApi;
        this.cleanRestaurantDelegate = cleanRestaurantDelegate;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<RestaurantDetails> getRestaurantDetails(@Nullable String placeId) {
        if (placeId == null) {
            return new MutableLiveData<>();
        }

        final MutableLiveData<RestaurantDetails> detailsRestaurantMutableLiveData = new MutableLiveData<>();

        // Check if the request has already been executed
        final RestaurantDetails cachedRestaurantsDetails = detailsCache.get(placeId);

        if (cachedRestaurantsDetails != null) {
            Log.d("Neige", "REPO getRestaurantDetails: from cache");
            detailsRestaurantMutableLiveData.setValue(cachedRestaurantsDetails);
        } else {
            placesApi.getRestaurantDetails(placeId).enqueue(new Callback<RawDetailsResponse>() {
                @Override
                public void onResponse(
                    @NonNull Call<RawDetailsResponse> call,
                    @NonNull Response<RawDetailsResponse> response
                ) {
                    if (response.isSuccessful()) {
                        final RestaurantDetails restaurantDetails = cleanDataFromRetrofit(response.body());

                        if (restaurantDetails != null) {
                            Log.d("Neige", "REPO getRestaurantDetails: from API");
                            detailsRestaurantMutableLiveData.setValue(restaurantDetails);
                            detailsCache.put(placeId, restaurantDetails);
                        }
                    }
                }

                @Override
                public void onFailure(
                    @NonNull Call<RawDetailsResponse> call,
                    @NonNull Throwable t
                ) {
                }
            });
        }

        return detailsRestaurantMutableLiveData;
    }

    // ---------------------------------- CLEAN RESPONSE METHODS -----------------------------------

    @Nullable
    private RestaurantDetails cleanDataFromRetrofit(@Nullable RawDetailsResponse rawDetailsResponse) {
        if (rawDetailsResponse == null || rawDetailsResponse.getResult() == null) {
            return null;
        }

        final RawDetailsResponse.Result result = rawDetailsResponse.getResult();
        if (result.getPlaceId() == null || result.getBusinessStatus() == null ||
            !result.getBusinessStatus().equals("OPERATIONAL") ||
            result.getGeometry() == null || result.getGeometry().getLocation() == null ||
            result.getGeometry().getLocation().getLat() == null ||
            result.getGeometry().getLocation().getLng() == null ||
            result.getName() == null || result.getFormattedAddress() == null
        ) {
            return null;
        }

        final String photoUrl;
        if (result.getPhotos() == null || result.getPhotos().isEmpty()) {
            photoUrl = null;
        } else {
            photoUrl = cleanRestaurantDelegate.getPhotoUrl(result.getPhotos().get(0).getPhotoReference());
        }

        return new RestaurantDetails(
            result.getPlaceId(),
            result.getName(),
            result.getFormattedAddress(),
            cleanRestaurantDelegate.getRating(result.getRating()),
            photoUrl,
            result.getInternationalPhoneNumber(),
            result.getWebsite(),
            setupOpeningHours(result.getOpeningHours())
        );

    }

    @NonNull
    private List<RestaurantHour> setupOpeningHours(@Nullable RawDetailsResponse.OpeningHours openingHours) {
        if (openingHours == null || openingHours.getPeriods() == null) {
            return Collections.emptyList();
        }
        final List<RestaurantHour> restaurantHourList = new ArrayList<>();

        for (RawDetailsResponse.Period period : openingHours.getPeriods()) {
            final RawDetailsResponse.Open open = period.getOpen();
            final RawDetailsResponse.Close close = period.getClose();

            if (open != null && open.getDay() != null && open.getTime() != null) {
                restaurantHourList.add(new RestaurantHour(
                    true,
                    getDayOfWeek(open.getDay()),
                    getTime(open.getTime())
                ));
            }

            if (close != null && close.getDay() != null && close.getTime() != null) {
                restaurantHourList.add(new RestaurantHour(
                    false,
                    getDayOfWeek(close.getDay()),
                    getTime(close.getTime())
                ));
            }
        }

        return restaurantHourList;
    }

    /**
     * Converts the day from Places API standard to java.time standard.<br />
     * The day starts at Monday=1 for java.time and Sunday=0 for Places API.
     */
    @NonNull
    private DayOfWeek getDayOfWeek(int day) {
        return DayOfWeek.of(day == 0 ? 7 : day);
    }

    @NonNull
    private LocalTime getTime(@NonNull String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
    }
}
