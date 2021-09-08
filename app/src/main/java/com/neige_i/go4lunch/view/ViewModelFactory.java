package com.neige_i.go4lunch.view;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neige_i.go4lunch.data.firebase.FirebaseRepository;
import com.neige_i.go4lunch.data.firebase.FirebaseRepositoryImpl;
import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.FirestoreRepositoryImpl;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepositoryImpl;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepositoryImpl;
import com.neige_i.go4lunch.data.location.LocationPermissionRepository;
import com.neige_i.go4lunch.data.location.LocationPermissionRepositoryImpl;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.data.location.LocationRepositoryImpl;
import com.neige_i.go4lunch.domain.to_sort.GetFirestoreUserListUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsItemUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsListUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.UpdateInterestedWorkmatesUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.UpdateSelectedRestaurantUseCaseImpl;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCaseImpl;
import com.neige_i.go4lunch.domain.firestore.CreateFirestoreUserUseCaseImpl;
import com.neige_i.go4lunch.domain.firestore.GetFirestoreUserUseCaseImpl;
import com.neige_i.go4lunch.domain.location.GetGpsResolvableUseCaseImpl;
import com.neige_i.go4lunch.domain.location.GetGpsStatusUseCaseImpl;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCaseImpl;
import com.neige_i.go4lunch.domain.location.GetLocationUseCaseImpl;
import com.neige_i.go4lunch.domain.location.RequestGpsUseCaseImpl;
import com.neige_i.go4lunch.domain.location.SetLocationPermissionUseCaseImpl;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCaseImpl;
import com.neige_i.go4lunch.domain.place_nearby.GetNearbyRestaurantsUseCaseImpl;
import com.neige_i.go4lunch.view.auth.AuthViewModel;
import com.neige_i.go4lunch.view.detail.DetailViewModel;
import com.neige_i.go4lunch.view.dispatcher.DispatcherViewModel;
import com.neige_i.go4lunch.view.home.HomeViewModel;
import com.neige_i.go4lunch.view.list_restaurant.RestaurantListViewModel;
import com.neige_i.go4lunch.view.list_workmate.WorkmateListViewModel;
import com.neige_i.go4lunch.view.map.MapViewModel;

import java.time.Clock;
import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory {

    // -------------------------------------  CLASS VARIABLES --------------------------------------

    @NonNull
    private final NearbyRepository nearbyRepository;
    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;
    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final DetailsRepository detailsRepository;
    @NonNull
    private final FirebaseRepository firebaseRepository;
    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final Clock clock;
    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Nullable
    private static ViewModelFactory factory;

    public ViewModelFactory(@NonNull NearbyRepository nearbyRepository, @NonNull LocationPermissionRepository locationPermissionRepository,
                            @NonNull LocationRepository locationRepository, @NonNull DetailsRepository detailsRepository,
                            @NonNull FirebaseRepository firebaseRepository, @NonNull FirestoreRepository firestoreRepository
    ) {
        this.nearbyRepository = nearbyRepository;
        this.locationPermissionRepository = locationPermissionRepository;
        this.locationRepository = locationRepository;
        this.detailsRepository = detailsRepository;
        this.firebaseRepository = firebaseRepository;
        this.firestoreRepository = firestoreRepository;
        clock = Clock.systemDefaultZone();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // -------------------------------------- FACTORY METHODS --------------------------------------

    @NonNull
    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(
                        // Instantiate repositories here to make sure only one instance of them exists
                        new NearbyRepositoryImpl(
                            Executors.newSingleThreadExecutor(),
                            new Handler(Looper.getMainLooper())
                        ),
                        new LocationPermissionRepositoryImpl(),
                        new LocationRepositoryImpl(),
                        new DetailsRepositoryImpl(
                            Executors.newSingleThreadExecutor(),
                            new Handler(Looper.getMainLooper())
                        ),
                        new FirebaseRepositoryImpl(),
                        new FirestoreRepositoryImpl(FirebaseFirestore.getInstance())
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
        if (modelClass.isAssignableFrom(DispatcherViewModel.class)) {
            return (T) new DispatcherViewModel(new GetFirebaseUserUseCaseImpl(firebaseAuth));
        } else if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(
                firebaseAuth,
                new GetFirestoreUserUseCaseImpl(firestoreRepository),
                new CreateFirestoreUserUseCaseImpl(firestoreRepository)
            );
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(
                new GetLocationPermissionUseCaseImpl(locationPermissionRepository),
                new SetLocationPermissionUseCaseImpl(locationPermissionRepository),
                new SetLocationUpdatesUseCaseImpl(locationRepository),
                new GetGpsResolvableUseCaseImpl(locationRepository)
            );
        } else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(
                new GetLocationPermissionUseCaseImpl(locationPermissionRepository),
                new GetLocationUseCaseImpl(locationRepository),
                new GetNearbyRestaurantsUseCaseImpl(locationRepository, nearbyRepository),
                new GetGpsStatusUseCaseImpl(locationRepository),
                new RequestGpsUseCaseImpl(locationRepository)
            );
        } else if (modelClass.isAssignableFrom(RestaurantListViewModel.class)) {
            return (T) new RestaurantListViewModel(new GetRestaurantDetailsListUseCaseImpl(
                locationRepository,
                nearbyRepository,
                detailsRepository,
                firestoreRepository
            ));
        } else if (modelClass.isAssignableFrom(WorkmateListViewModel.class)) {
            return (T) new WorkmateListViewModel(
                new GetFirestoreUserListUseCaseImpl(firestoreRepository),
                clock
            );
        } else if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(
                new GetRestaurantDetailsItemUseCaseImpl(detailsRepository, firebaseRepository),
                new UpdateInterestedWorkmatesUseCaseImpl(firestoreRepository),
                new UpdateSelectedRestaurantUseCaseImpl(firestoreRepository),
                new GetFirebaseUserUseCaseImpl(firebaseAuth),
                clock,
                firestoreRepository
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
