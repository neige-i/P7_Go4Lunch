package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.repository.firestore.FirestoreRepository;
import com.neige_i.go4lunch.repository.firestore.FirestoreRepositoryImpl;
import com.neige_i.go4lunch.repository.location.LocationPermissionRepository;
import com.neige_i.go4lunch.repository.location.LocationPermissionRepositoryImpl;
import com.neige_i.go4lunch.repository.location.LocationRepository;
import com.neige_i.go4lunch.repository.location.LocationRepositoryImpl;
import com.neige_i.go4lunch.repository.preferences.PreferencesRepository;
import com.neige_i.go4lunch.repository.preferences.PreferencesRepositoryImpl;

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
    public abstract PreferencesRepository bindPreferencesRepository(
        PreferencesRepositoryImpl preferencesRepositoryImpl
    );
}
