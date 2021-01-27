package com.neige_i.go4lunch.view.map;

import java.util.Objects;

public class MapViewState {

    private final String placeId;
    private final String name;
    private final double latitude;
    private final double longitude;
    // image


    public MapViewState(String placeId, String name, double latitude, double longitude) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapViewState that = (MapViewState) o;
        return Objects.equals(placeId, that.placeId) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name);
    }

    @Override
    public String toString() {
        return "MapViewState{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
