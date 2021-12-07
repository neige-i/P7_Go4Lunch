package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.google_places.AutocompleteRepository;
import com.neige_i.go4lunch.data.google_places.RawAutocompleteQuery;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.data.location.LocationRepository;

import java.util.List;

import javax.inject.Inject;

public class GetAutocompleteResultsUseCaseImpl implements GetAutocompleteResultsUseCase {

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final AutocompleteRepository autocompleteRepository;

    @Inject
    public GetAutocompleteResultsUseCaseImpl(
        @NonNull LocationRepository locationRepository,
        @NonNull AutocompleteRepository autocompleteRepository
    ) {
        this.locationRepository = locationRepository;
        this.autocompleteRepository = autocompleteRepository;
    }

    @NonNull
    @Override
    public LiveData<List<AutocompleteRestaurant>> get(@NonNull String searchQuery) {
        return Transformations.switchMap(locationRepository.getCurrentLocation(), location -> {
            return autocompleteRepository.getData(new RawAutocompleteQuery(searchQuery, location));
        });
    }
}
