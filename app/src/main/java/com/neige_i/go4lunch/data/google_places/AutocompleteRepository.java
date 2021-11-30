package com.neige_i.go4lunch.data.google_places;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawAutocompleteResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class AutocompleteRepository extends PlacesRepository<RawAutocompleteQuery, RawAutocompleteResponse, List<AutocompleteRestaurant>> {

    @NonNull
    private final MutableLiveData<String> currentSearchQueryMutableLiveData = new MutableLiveData<>();

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    AutocompleteRepository(@NonNull PlacesApi placesApi, @NonNull String mapsApiKey) {
        super(placesApi, mapsApiKey);
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    Call<RawAutocompleteResponse> getRequest(@NonNull RawAutocompleteQuery rawAutocompleteQuery) {
        return placesApi.getRestaurantsByName(
            rawAutocompleteQuery.getSearchQuery(),
            getLocationString(rawAutocompleteQuery.getLocation())
        );
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

    @NonNull
    public LiveData<String> getCurrentSearchQuery() {
        return currentSearchQueryMutableLiveData;
    }

    public void setCurrentSearch(@Nullable String autocompleteRestaurants) {
        currentSearchQueryMutableLiveData.setValue(autocompleteRestaurants);
    }
}
