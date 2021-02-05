package com.neige_i.go4lunch.view.map;

import com.google.android.gms.maps.CameraUpdate;

import java.util.List;

public class MapViewState {

    private final boolean locationLayerEnabled;
    private final List<MarkerViewState> markerViewStates;
    private final CameraUpdate mapCamera;


    public MapViewState(boolean locationLayerEnabled, List<MarkerViewState> markerViewStates, CameraUpdate mapCamera) {
        this.locationLayerEnabled = locationLayerEnabled;
        this.markerViewStates = markerViewStates;
        this.mapCamera = mapCamera;
    }

    public boolean isLocationLayerEnabled() {
        return locationLayerEnabled;
    }

    public List<MarkerViewState> getMarkerViewStates() {
        return markerViewStates;
    }

    public CameraUpdate getMapCamera() {
        return mapCamera;
    }
}
