package com.neige_i.go4lunch.data.google_places;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {

    @GET("maps/api/place/nearbysearch/json?radius=2000&type=restaurant&key=" + BuildConfig.MAPS_API_KEY)
    Call<NearbyResponse> getNearbyRestaurants(@Query("location") String location);

    static PlacesApi getInstance() {
        return new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacesApi.class);
    }
}
