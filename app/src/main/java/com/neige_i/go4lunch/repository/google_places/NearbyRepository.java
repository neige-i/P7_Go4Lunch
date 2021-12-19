package com.neige_i.go4lunch.repository.google_places;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.PlacesApi;
import com.neige_i.go4lunch.repository.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.repository.google_places.model.RawNearbyResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class NearbyRepository extends PlacesRepository<Location, RawNearbyResponse, List<NearbyRestaurant>> {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final PlacesApi placesApi;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    NearbyRepository(@NonNull PlacesApi placesApi, @NonNull String mapsApiKey) {
        super(placesApi, mapsApiKey);
        this.placesApi = placesApi;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    Call<RawNearbyResponse> getRequest(@NonNull Location location) {
        return placesApi.getNearbyRestaurants(getLocationString(location));
    }

    @Nullable
    @Override
    List<NearbyRestaurant> cleanDataFromRetrofit(@Nullable RawNearbyResponse rawNearbyResponse) {
        if (rawNearbyResponse == null || rawNearbyResponse.getResults() == null) {
            return null;
        }

        final List<NearbyRestaurant> nearbyRestaurants = new ArrayList<>();

        for (RawNearbyResponse.Result result : rawNearbyResponse.getResults()) {
            if (result.getPlaceId() != null && result.getBusinessStatus() != null &&
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
                    photoUrl = getPhotoUrl(result.getPhotos().get(0).getPhotoReference());
                }

                nearbyRestaurants.add(new NearbyRestaurant(
                    result.getPlaceId(),
                    result.getName(),
                    getAddress(result.getVicinity()),
                    result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng(),
                    getRating(result.getRating()),
                    photoUrl
                ));
            }
        }

        return nearbyRestaurants;
    }
}
