package com.neige_i.go4lunch.di;

import android.app.Application;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.data.google_places.PlacesApi;

import java.time.Clock;
import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class FrameworkProvidingModule {

    // ---------------------------------------- JAVA TIME ----------------------------------------

    @Provides
    public static Clock provideClock() {
        return Clock.systemDefaultZone();
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

    // --------------------------------------- BUILD CONFIG ----------------------------------------

    @Provides
    public static String provideMapsApiKey() {
        return BuildConfig.MAPS_API_KEY;
    }

    // ------------------------------------------ LOCALE -------------------------------------------

    @Provides
    public static Locale provideDefaultLocale() {
        return Locale.getDefault();
    }

    // ------------------------------------- ANDROID LOCATION --------------------------------------

    @Provides
    public static Location provideLocation() {
        return new Location("");
    }

    // ---------------------------------------- ANDROID OS -----------------------------------------

    @Provides
    public static Looper provideMainLooper() {
        return Looper.getMainLooper();
    }
}
