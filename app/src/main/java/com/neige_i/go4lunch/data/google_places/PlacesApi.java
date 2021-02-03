package com.neige_i.go4lunch.data.google_places;

import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {

    static PlacesApi getInstance() {
        return new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacesApi.class);
    }

    @GET("maps/api/place/nearbysearch/json?" +
        "radius=2000" +
        "&type=restaurant" +
        "&key=AIzaSyDdc24VwRd5iGQjd46ygpOIcVGhiDnD4gs"
    )
    Call<NearbyResponse> getNearbyRestaurants(@Query("location") String location);

    @GET("maps/api/place/details/json?" +
        "fields=formatted_address,formatted_phone_number,geometry/location,name,opening_hours,photos,place_id,rating,website" +
        "&key=AIzaSyDdc24VwRd5iGQjd46ygpOIcVGhiDnD4gs"
    )
    Call<DetailsResponse> getRestaurantDetails(@Query("place_id") String placeId);
}
