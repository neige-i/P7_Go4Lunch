package com.neige_i.go4lunch.data.google_places;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawAutocompleteResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class AutocompleteRepository extends PlacesRepository<RawAutocompleteResponse, List<AutocompleteRestaurant>> {

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    AutocompleteRepository(@NonNull PlacesApi placesApi, @NonNull String mapsApiKey) {
        super(placesApi, mapsApiKey);
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    List<String> toQueryStrings(@NonNull Object... queryParameter) {
        return Arrays.asList(
            (String) queryParameter[0],
            getLocationString((Location) queryParameter[1])
        );
    }

    @NonNull
    @Override
    Call<RawAutocompleteResponse> getRequest(@NonNull List<String> queryParameters) {
        return placesApi.getRestaurantsByName(queryParameters.get(0), queryParameters.get(1));
    }

    @NonNull
    @Override
    String getNameForLog() {
        return "Autocomplete";
    }

    @Nullable
    @Override
    List<AutocompleteRestaurant> cleanDataFromRetrofit(@Nullable RawAutocompleteResponse rawAutocompleteResponse) {
        if (rawAutocompleteResponse == null || rawAutocompleteResponse.getPredictions() == null) {
            return null;
        }

        final List<AutocompleteRestaurant> autocompleteRestaurants = new ArrayList<>();

        for (RawAutocompleteResponse.Prediction prediction : rawAutocompleteResponse.getPredictions()) {
            if (prediction.getPlaceId() != null &&
                prediction.getTypes() != null &&
                prediction.getTypes().contains("restaurant") &&
                prediction.getDescription() != null
            ) {
                autocompleteRestaurants.add(new AutocompleteRestaurant(
                    prediction.getPlaceId(),
                    getAddress(prediction.getDescription())
                ));
            }
        }

        return autocompleteRestaurants;
    }
}
