package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;

import java.util.List;

public interface GetAutocompleteResultsUseCase {

    @NonNull
    LiveData<List<AutocompleteRestaurant>> get(@NonNull String searchQuery);
}
