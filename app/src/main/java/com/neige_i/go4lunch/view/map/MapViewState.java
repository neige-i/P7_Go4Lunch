package com.neige_i.go4lunch.view.map;

import java.util.Objects;

public class MapViewState {

    private final String placeId;
    private final String name;
    // location
    // image


    public MapViewState(String placeId, String name) {
        this.placeId = placeId;
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
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
