package com.neige_i.go4lunch.view;

import androidx.annotation.NonNull;

/**
 * Callback interface to start {@link com.neige_i.go4lunch.view.detail.DetailActivity DetailActivity}.
 */
public interface StartDetailActivityCallback {

    void showDetailedInfo(@NonNull String placeId);
}
