package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.google_places.AutocompleteRepository;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;

import javax.inject.Inject;

public class SetSearchQueryUseCaseImpl implements SetSearchQueryUseCase {

    @NonNull
    private final AutocompleteRepository autocompleteRepository;

    @Inject
    public SetSearchQueryUseCaseImpl(@NonNull AutocompleteRepository autocompleteRepository) {
        this.autocompleteRepository = autocompleteRepository;
    }

    @Override
    public void launch(@NonNull AutocompleteRestaurant searchQuery) {
        autocompleteRepository.setCurrentSearch(searchQuery);
    }

    @Override
    public void close() {
        autocompleteRepository.setCurrentSearch(null);
    }
}
