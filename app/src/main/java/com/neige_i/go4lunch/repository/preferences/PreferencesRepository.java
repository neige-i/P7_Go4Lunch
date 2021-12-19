package com.neige_i.go4lunch.repository.preferences;

public interface PreferencesRepository {

    boolean getMiddayNotificationEnabled();

    void setMiddayNotificationEnabled(boolean isEnabled);
}
