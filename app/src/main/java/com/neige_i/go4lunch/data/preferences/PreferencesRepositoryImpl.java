package com.neige_i.go4lunch.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class PreferencesRepositoryImpl implements PreferencesRepository {

    @NonNull
    static final String FILE_PREFERENCES = "settings";
    @NonNull
    static final String KEY_MIDDAY_NOTIFICATION = "middayNotification";

    @NonNull
    private final SharedPreferences sharedPreferences;

    @Inject
    PreferencesRepositoryImpl(@ApplicationContext @NonNull Context applicationContext) {
        sharedPreferences = applicationContext.getSharedPreferences(FILE_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public boolean getMiddayNotificationEnabled() {
        return sharedPreferences.getBoolean(KEY_MIDDAY_NOTIFICATION, true);
    }

    @Override
    public void setMiddayNotificationEnabled(boolean isEnabled) {
        sharedPreferences.edit().putBoolean(KEY_MIDDAY_NOTIFICATION, isEnabled).apply();
    }
}
