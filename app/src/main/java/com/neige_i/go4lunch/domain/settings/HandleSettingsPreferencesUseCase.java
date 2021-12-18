package com.neige_i.go4lunch.domain.settings;

public interface HandleSettingsPreferencesUseCase {

    boolean getMiddayNotification();

    void setMiddayNotification(boolean isEnabled);
}
