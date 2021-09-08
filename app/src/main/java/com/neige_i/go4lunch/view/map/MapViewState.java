package com.neige_i.go4lunch.view.map;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

class MapViewState {

    private final boolean locationLayerEnabled;
    private final boolean fabVisible;
    @DrawableRes
    private final int fabDrawable;
    @ColorRes
    private final int fabColor;
    @Nullable
    private final List<MarkerViewState> markersToAdd;
    @Nullable
    private final Double mapLatitude;
    @Nullable
    private final Double mapLongitude;
    @Nullable
    private final Float mapZoom;


    MapViewState(boolean locationLayerEnabled,
                 boolean fabVisible,
                 int fabDrawable,
                 int fabColor,
                 @Nullable List<MarkerViewState> markersToAdd,
                 @Nullable Double mapLatitude,
                 @Nullable Double mapLongitude,
                 @Nullable Float mapZoom
    ) {
        this.locationLayerEnabled = locationLayerEnabled;
        this.fabVisible = fabVisible;
        this.fabDrawable = fabDrawable;
        this.fabColor = fabColor;
        this.markersToAdd = markersToAdd;
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

    public int getFabDrawable() {
        return fabDrawable;
    }

    public int getFabColor() {
        return fabColor;
    }

    @Nullable
    public List<MarkerViewState> getMarkersToAdd() {
        return markersToAdd;
    }

    @Nullable
    public Double getMapLatitude() {
        return mapLatitude;
    }

    @Nullable
    public Double getMapLongitude() {
        return mapLongitude;
    }

    @Nullable
    public Float getMapZoom() {
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
            Objects.equals(markersToAdd, that.markersToAdd) &&
            Objects.equals(mapLatitude, that.mapLatitude) &&
            Objects.equals(mapLongitude, that.mapLongitude) &&
            Objects.equals(mapZoom, that.mapZoom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationLayerEnabled, fabVisible, fabDrawable, fabColor, markersToAdd, mapLatitude, mapLongitude, mapZoom);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapViewState{" +
            "locationLayerEnabled=" + locationLayerEnabled +
            ", FAB{visible=" + fabVisible + ", drawable=" + fabDrawable + ", color=" + fabColor + '}' +
            ", markersToAdd[" + (markersToAdd != null ? markersToAdd.size() : null) + ']' +
            ", map{" + mapLatitude + ", " + mapLongitude + ", " + mapZoom + '}' +
            '}';
    }
}
