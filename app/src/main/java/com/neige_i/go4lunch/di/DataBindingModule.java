package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;
import com.neige_i.go4lunch.data.firebase.FirebaseRepositoryImpl;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.FirestoreRepositoryImpl;
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
public abstract class DataBindingModule {

    @Binds
    public abstract LocationPermissionRepository bindLocationPermissionRepository(
        LocationPermissionRepositoryImpl locationPermissionRepositoryImpl
    );

    @Binds
    public abstract LocationRepository bindLocationRepository(
        LocationRepositoryImpl locationRepositoryImpl
    );

    @Binds
    public abstract FirestoreRepository firestoreRepository(
        FirestoreRepositoryImpl firestoreRepositoryImpl
    );

    @Binds
    public abstract FirebaseRepository bindFirebaseRepository(
        FirebaseRepositoryImpl firebaseRepositoryImpl
    );

    @Binds
    public abstract com.neige_i.go4lunch.data.firebase.FirestoreRepository bindFirestoreRepository(
        com.neige_i.go4lunch.data.firebase.FirestoreRepositoryImpl firestoreRepositoryImpl
    );
}
