package com.neige_i.go4lunch.view.map;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

class MapViewState {

    private final boolean locationLayerEnabled;
    private final boolean fabVisible;
    @DrawableRes
    private final int fabDrawable;
    @ColorRes
    private final int fabColor;
    @NonNull
    private final List<MarkerViewState> markers;
    private final double mapLatitude;
    private final double mapLongitude;
    private final float mapZoom;

    MapViewState(
        boolean locationLayerEnabled,
        boolean fabVisible,
        @DrawableRes int fabDrawable,
        @ColorRes int fabColor,
        @NonNull List<MarkerViewState> markers,
        double mapLatitude,
        double mapLongitude,
        float mapZoom
    ) {
        this.locationLayerEnabled = locationLayerEnabled;
        this.fabVisible = fabVisible;
        this.fabDrawable = fabDrawable;
        this.fabColor = fabColor;
        this.markers = markers;
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
        this.mapZoom = mapZoom;
    }

    public boolean isLocationLayerEnabled() {
        return locationLayerEnabled;
    }

    public boolean isFabVisible() {
        return fabVisible;
    }

    @DrawableRes
    public int getFabDrawable() {
        return fabDrawable;
    }

    @ColorRes
    public int getFabColor() {
        return fabColor;
    }

    @NonNull
    public List<MarkerViewState> getMarkers() {
        return markers;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapViewState that = (MapViewState) o;
        return locationLayerEnabled == that.locationLayerEnabled &&
            fabVisible == that.fabVisible &&
            fabDrawable == that.fabDrawable &&
            fabColor == that.fabColor &&
            Double.compare(that.mapLatitude, mapLatitude) == 0 &&
            Double.compare(that.mapLongitude, mapLongitude) == 0 &&
            Float.compare(that.mapZoom, mapZoom) == 0 &&
            markers.equals(that.markers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationLayerEnabled, fabVisible, fabDrawable, fabColor, markers, mapLatitude, mapLongitude, mapZoom);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapViewState{" +
            "locationLayerEnabled=" + locationLayerEnabled +
            ", FAB{visible=" + fabVisible + ", drawable=" + fabDrawable + ", color=" + fabColor + '}' +
            ", markersCount=" + markers +
            ", map{" + mapLatitude + ", " + mapLongitude + ", " + mapZoom + '}' +
            '}';
    }
}
