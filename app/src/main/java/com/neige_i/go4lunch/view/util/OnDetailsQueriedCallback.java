package com.neige_i.go4lunch.view.util;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.view.list_restaurant.RestaurantListFragment;

/**
 * Callback interface used in {@link com.neige_i.go4lunch.view.map.MapFragment MapFragment}
 * or {@link RestaurantListFragment ListFragment} when the user wants to see the details of a restaurant.<br />
 * The specified placeId is passed to {@link com.neige_i.go4lunch.view.home.HomeActivity HomeActivity}
 * which is in charge to start {@link com.neige_i.go4lunch.view.detail.DetailActivity DetailActivity}.
 */
public interface OnDetailsQueriedCallback {
    void onDetailsQueried(@NonNull String placeId);
}
