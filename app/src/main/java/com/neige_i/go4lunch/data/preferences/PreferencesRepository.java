package com.neige_i.go4lunch.data.preferences;

public interface PreferencesRepository {

    boolean getMiddayNotificationEnabled();

    void setMiddayNotificationEnabled(boolean isEnabled);
}
