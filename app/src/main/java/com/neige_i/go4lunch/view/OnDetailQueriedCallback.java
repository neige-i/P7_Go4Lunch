package com.neige_i.go4lunch.view;

/**
 * Callback interface used in {@link com.neige_i.go4lunch.view.map.MapFragment MapFragment}
 * or {@link com.neige_i.go4lunch.view.list.ListFragment ListFragment} when the user wants to see the details of a restaurant.<br />
 * The specified placeId is passed to {@link com.neige_i.go4lunch.view.home.HomeActivity HomeActivity}
 * which is in charge to start {@link com.neige_i.go4lunch.view.detail.DetailActivity DetailActivity}.
 */
public interface OnDetailQueriedCallback {
    void onDetailQueried(String placeId);
}
