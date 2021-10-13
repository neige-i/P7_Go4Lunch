package com.neige_i.go4lunch.di;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neige_i.go4lunch.data.google_places.PlacesApi;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ProvideModule {

    // ---------------------------------------- JAVA TIME ----------------------------------------

    @Provides
    public static Clock provideClock() {
        return Clock.systemDefaultZone();
    }

    // -------------------------------------- BACKGROUND WORK --------------------------------------

    @Provides
    public static ExecutorService provideExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    public static Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }

    // ----------------------------------------- RETROFIT ------------------------------------------

    @Provides
    public static PlacesApi providePlacesApi() {
        return new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacesApi.class);
    }

    // -------------------------------------- GOOGLE LOCATION --------------------------------------

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

    // ----------------------------------------- FIREBASE ------------------------------------------

    @Provides
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    public static FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }
}
