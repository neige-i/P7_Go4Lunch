package com.neige_i.go4lunch.domain.settings;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.repository.preferences.PreferencesRepository;

import javax.inject.Inject;

public class HandleSettingsPreferencesUseCaseImpl implements HandleSettingsPreferencesUseCase {

    @NonNull
    private final PreferencesRepository preferencesRepository;

    @Inject
    public HandleSettingsPreferencesUseCaseImpl(@NonNull PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    @Override
    public boolean getMiddayNotification() {
        return preferencesRepository.getMiddayNotificationEnabled();
    }

    @Override
    public void setMiddayNotification(boolean isEnabled) {
        preferencesRepository.setMiddayNotificationEnabled(isEnabled);
    }
}
