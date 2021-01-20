package com.neige_i.go4lunch.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.data.google_places.PlacesRepository;
import com.neige_i.go4lunch.view.home.HomeViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    // -------------------------------------  CLASS VARIABLES --------------------------------------

    @Nullable
    private static ViewModelFactory factory;

    // -------------------------------------- FACTORY METHODS --------------------------------------

    @NonNull
    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    // -------------------------------- VIEW MODEL FACTORY METHODS ---------------------------------

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(new PlacesRepository());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
