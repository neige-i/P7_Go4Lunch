package com.neige_i.go4lunch.view;

import androidx.annotation.NonNull;

/**
 * Callback interface used when the details of a restaurant are queried and need to be shown.
 */
public interface OnDetailsQueriedCallback {

    void onDetailsQueried(@NonNull String placeId);
}
