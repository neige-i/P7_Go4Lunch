package com.neige_i.go4lunch.di;

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

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    public abstract LocationPermissionRepository bindLocationPermissionRepository(
        LocationPermissionRepositoryImpl locationPermissionRepositoryImpl
    );

    @Binds
    public abstract LocationRepository bindLocationRepository(
        LocationRepositoryImpl locationRepositoryImpl
    );

    @Binds
    public abstract NearbyRepository bindNearbyRepository(
        NearbyRepositoryImpl nearbyRepositoryImpl
    );

    @Binds
    public abstract DetailsRepository bindDetailsRepository(
        DetailsRepositoryImpl detailsRepositoryImpl
    );

    @Binds
    public abstract FirebaseRepository bindFirebaseRepository(
        FirebaseRepositoryImpl firebaseRepositoryImpl
    );

    @Binds
    public abstract FirestoreRepository bindFirestoreRepository(
        FirestoreRepositoryImpl firestoreRepositoryImpl
    );
}
