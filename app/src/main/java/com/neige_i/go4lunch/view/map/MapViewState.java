package com.neige_i.go4lunch.view.map;

import java.util.Objects;

public class MapViewState {

    private final String placeId;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String vicinity;
    // image


    public MapViewState(String placeId, String name, double latitude, double longitude, String vicinity) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vicinity = vicinity;
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

    public String getVicinity() {
        return vicinity;
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
