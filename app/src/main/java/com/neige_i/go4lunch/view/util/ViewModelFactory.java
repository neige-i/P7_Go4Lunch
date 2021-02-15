package com.neige_i.go4lunch.view.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;
import com.neige_i.go4lunch.data.firebase.FirebaseRepositoryImpl;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepositoryImpl;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepositoryImpl;
import com.neige_i.go4lunch.data.location.LocationRepositoryImpl;
import com.neige_i.go4lunch.domain.GetAllRestaurantDetailsUseCaseImpl;
import com.neige_i.go4lunch.domain.GetNearbyRestaurantsUseCaseImpl;
import com.neige_i.go4lunch.domain.ToggleFavRestaurantUseCaseImpl;
import com.neige_i.go4lunch.domain.UpdateLocPermissionUseCaseImpl;
import com.neige_i.go4lunch.domain.GetLocPermissionUseCaseImpl;
import com.neige_i.go4lunch.domain.StopLocationUpdatesUseCaseImpl;
import com.neige_i.go4lunch.domain.UpdateSelectedRestaurantUseCaseImpl;
import com.neige_i.go4lunch.view.detail.DetailViewModel;
import com.neige_i.go4lunch.view.home.HomeViewModel;
import com.neige_i.go4lunch.view.list.ListViewModel;
import com.neige_i.go4lunch.view.map.MapViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    // -------------------------------------  CLASS VARIABLES --------------------------------------

    @NonNull
    private final NearbyRepository nearbyRepository;
    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final DetailsRepository detailsRepository;
    @NonNull
    private final FirebaseRepository firebaseRepository;

    @Nullable
    private static ViewModelFactory factory;

    public ViewModelFactory(@NonNull NearbyRepository nearbyRepository, @NonNull LocationRepository locationRepository, @NonNull DetailsRepository detailsRepository, @NonNull FirebaseRepository firebaseRepository) {
        this.nearbyRepository = nearbyRepository;
        this.locationRepository = locationRepository;
        this.detailsRepository = detailsRepository;
        this.firebaseRepository = firebaseRepository;
    }

    // -------------------------------------- FACTORY METHODS --------------------------------------

    @NonNull
    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(
                        // Instantiate repositories here to make sure only one instance of them exists
                        new NearbyRepositoryImpl(),
                        new LocationRepositoryImpl(),
                        new DetailsRepositoryImpl(),
                        new FirebaseRepositoryImpl()
                    );
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
            return (T) new HomeViewModel(
                new GetLocPermissionUseCaseImpl(locationRepository),
                new UpdateLocPermissionUseCaseImpl(locationRepository),
                new StopLocationUpdatesUseCaseImpl(locationRepository)
            );
        } else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(new GetNearbyRestaurantsUseCaseImpl(locationRepository, nearbyRepository));
        } else if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(
                new GetAllRestaurantDetailsUseCaseImpl(detailsRepository, firebaseRepository),
                new ToggleFavRestaurantUseCaseImpl(firebaseRepository),
                new UpdateSelectedRestaurantUseCaseImpl(firebaseRepository)
            );
        } else if (modelClass.isAssignableFrom(ListViewModel.class)) {
            return (T) new ListViewModel(locationRepository, nearbyRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
