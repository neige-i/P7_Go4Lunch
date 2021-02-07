package com.neige_i.go4lunch.data.google_places.model;

/**
 * Base class for Google API request responses.
 */
public abstract class PlacesResponse {
    public static abstract class Photo {
        public abstract String getPhotoReference();
    }
}
