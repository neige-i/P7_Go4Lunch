package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.repository.google_places.model.AutocompleteRestaurant;

public interface SetSearchQueryUseCase {

    void launch(@NonNull AutocompleteRestaurant searchQuery);

    void close();
}
