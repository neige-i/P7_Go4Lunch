package com.neige_i.go4lunch.data;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.repository.google_places.model.RawAutocompleteResponse;
import com.neige_i.go4lunch.repository.google_places.model.RawDetailsResponse;
import com.neige_i.go4lunch.repository.google_places.model.RawNearbyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {

    @GET("maps/api/place/nearbysearch/json?" +
        "radius=2000" +
        "&type=restaurant" +
        "&key=" + BuildConfig.MAPS_API_KEY
    )
    Call<RawNearbyResponse> getNearbyRestaurants(@Query("location") String location);

    @GET("maps/api/place/details/json?" +
        "fields=business_status,formatted_address,geometry/location,international_phone_number,name,opening_hours,photos,place_id,rating,website" +
        "&key=" + BuildConfig.MAPS_API_KEY
    )
    Call<RawDetailsResponse> getRestaurantDetails(@Query("place_id") String placeId);

    @GET("maps/api/place/autocomplete/json?" +
        "radius=2000" +
        "&strictbounds=true" +
        "&types=establishment" +
        "&key=" + BuildConfig.MAPS_API_KEY)
    Call<RawAutocompleteResponse> getRestaurantsByName(
        @Query("input") String searchQuery,
        @Query("location") String location
    );
}
