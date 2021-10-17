package com.neige_i.go4lunch.data.google_places;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawNearbyResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class NearbyRepositoryImpl implements NearbyRepository {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final PlacesApi placesApi;
    @NonNull
    private final CleanRestaurantDelegate cleanRestaurantDelegate;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final LruCache<String, List<NearbyRestaurant>> nearbyCache = new LruCache<>(4 * 1024 * 1024); // 4MB cache size

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public NearbyRepositoryImpl(
        @NonNull PlacesApi placesApi,
        @NonNull CleanRestaurantDelegate cleanRestaurantDelegate
    ) {
        this.placesApi = placesApi;
        this.cleanRestaurantDelegate = cleanRestaurantDelegate;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<List<NearbyRestaurant>> getNearbyRestaurants(@Nullable Location location) {
        if (location == null) {
            return new MutableLiveData<>();
        }

        final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();

        final String latLng = location.getLatitude() + "," + location.getLongitude();

        // Check if the request has already been executed
        final List<NearbyRestaurant> cachedNearbyRestaurants = nearbyCache.get(latLng);

        if (cachedNearbyRestaurants != null) {
            Log.d("Neige", "REPO getNearbyRestaurants: from cache");
            nearbyRestaurantsMutableLiveData.setValue(cachedNearbyRestaurants);
        } else {
            placesApi.getNearbyRestaurants(latLng).enqueue(new Callback<RawNearbyResponse>() {
                @Override
                public void onResponse(
                    @NonNull Call<RawNearbyResponse> call,
                    @NonNull Response<RawNearbyResponse> response
                ) {
                    if (response.isSuccessful()) {
                        final List<NearbyRestaurant> nearbyRestaurants = cleanDataFromRetrofit(response.body());

                        if (nearbyRestaurants != null) {
                            Log.d("Neige", "REPO getNearbyRestaurants: from API");
                            nearbyRestaurantsMutableLiveData.setValue(nearbyRestaurants);
                            nearbyCache.put(latLng, nearbyRestaurants);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RawNearbyResponse> call, @NonNull Throwable t) {
                }
            });
        }

        return nearbyRestaurantsMutableLiveData;
    }

    // ---------------------------------- CLEAN RESPONSE METHODS -----------------------------------

    @Nullable
    private List<NearbyRestaurant> cleanDataFromRetrofit(@Nullable RawNearbyResponse rawNearbyResponse) {
        if (rawNearbyResponse == null || rawNearbyResponse.getResults() == null) {
            return null;
        }

        final List<NearbyRestaurant> nearbyRestaurants = new ArrayList<>();

        for (RawNearbyResponse.Result result : rawNearbyResponse.getResults()) {
            if (result != null && result.getPlaceId() != null && result.getBusinessStatus() != null &&
                result.getBusinessStatus().equals("OPERATIONAL") &&
                result.getGeometry() != null && result.getGeometry().getLocation() != null &&
                result.getGeometry().getLocation().getLat() != null &&
                result.getGeometry().getLocation().getLng() != null &&
                result.getName() != null && result.getVicinity() != null
            ) {
                final String photoUrl;
                if (result.getPhotos() == null || result.getPhotos().isEmpty()) {
                    photoUrl = null;
                } else {
                    photoUrl = cleanRestaurantDelegate.getPhotoUrl(result.getPhotos().get(0).getPhotoReference());
                }

                nearbyRestaurants.add(new NearbyRestaurant(
                    result.getPlaceId(),
                    result.getName(),
                    result.getVicinity(),
                    result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng(),
                    cleanRestaurantDelegate.getRating(result.getRating()),
                    photoUrl
                ));
            }
        }

        return !nearbyRestaurants.isEmpty() ? nearbyRestaurants : null;
    }
}
