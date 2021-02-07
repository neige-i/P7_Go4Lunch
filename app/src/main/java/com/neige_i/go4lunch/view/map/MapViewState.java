package com.neige_i.go4lunch.view.map;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

class MapViewState {

    private final boolean locationLayerEnabled;
    private final List<MarkerViewState> markerViewStates;
    private final double mapLatitude;
    private final double mapLongitude;
    private final float mapZoom;


    public MapViewState(boolean locationLayerEnabled, List<MarkerViewState> markerViewStates, double mapLatitude, double mapLongitude, float mapZoom) {
        this.locationLayerEnabled = locationLayerEnabled;
        this.markerViewStates = markerViewStates;
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
        this.mapZoom = mapZoom;
    }

    public boolean isLocationLayerEnabled() {
        return locationLayerEnabled;
    }

    public List<MarkerViewState> getMarkerViewStates() {
        return markerViewStates;
    }

    public double getMapLatitude() {
        return mapLatitude;
    }

    public double getMapLongitude() {
        return mapLongitude;
    }

    public float getMapZoom() {
        return mapZoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapViewState that = (MapViewState) o;
        return locationLayerEnabled == that.locationLayerEnabled &&
            Double.compare(that.mapLatitude, mapLatitude) == 0 &&
            Double.compare(that.mapLongitude, mapLongitude) == 0 &&
            Float.compare(that.mapZoom, mapZoom) == 0 &&
            Objects.equals(markerViewStates, that.markerViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationLayerEnabled, markerViewStates, mapLatitude, mapLongitude, mapZoom);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapViewState{" +
            "location " + (locationLayerEnabled ? "enabled" : "disabled") +
            " " + markerViewStates +
            ", map(" + mapLatitude +
            "," + mapLongitude +
            "," + mapZoom + ')' +
            '}';
    }
}
