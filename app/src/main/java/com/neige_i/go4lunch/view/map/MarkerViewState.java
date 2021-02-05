package com.neige_i.go4lunch.view.map;

public class MarkerViewState {

    private final String placeId;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String vicinity;


    public MarkerViewState(String placeId, String name, double latitude, double longitude, String vicinity) {
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
}
