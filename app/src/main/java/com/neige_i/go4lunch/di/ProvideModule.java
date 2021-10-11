package com.neige_i.go4lunch.di;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ProvideModule {

    @Provides
    public static Clock provideClock() {
        return Clock.systemDefaultZone();
    }

    @Provides
    public static ExecutorService provideExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    public static Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    public static FusedLocationProviderClient provideFusedLocationProviderClient(
        Application application
    ) {
        return LocationServices.getFusedLocationProviderClient(application);
    }

    @Provides
    public static SettingsClient provideSettingsClient(Application application) {
        return LocationServices.getSettingsClient(application);
    }

    @Provides
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    public static FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Singleton // ASKME: binding/InstallIn/scope/ (scope only repo with cache)
    @Provides
    public static GpsStateChangeReceiver provideGpsStateChangeReceiver(Application application) {
        return new GpsStateChangeReceiver(
            (LocationManager) application.getSystemService(Context.LOCATION_SERVICE)
        );
    }
}
