package com.neige_i.go4lunch.data.google_places;

import static org.junit.Assert.*;

import android.location.Location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawNearbyResponse;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

public class PlacesRepositoryTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final String mapsApiKey = "MAPS_API_KEY";

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final PlacesRepository<Location, RawNearbyResponse, List<NearbyRestaurant>> placesRepository = new NearbyRepository(
        Mockito.mock(PlacesApi.class),
        mapsApiKey
    );

    // --------------------------------------- RATING TESTS ----------------------------------------

    @Test
    public void returnNegativeInt_when_ratingIsNull() {
        // WHEN
        final int rating = placesRepository.getRating(null);

        // THEN
        assertEquals(-1, rating);
    }

    @Test
    public void return0_when_ratingIs1() {
        // WHEN
        final int rating = placesRepository.getRating(1.);

        // THEN
        assertEquals(0, rating);
    }

    @Test
    public void return3_when_ratingIs5() {
        // WHEN
        final int rating = placesRepository.getRating(5.);

        // THEN
        assertEquals(3, rating);
    }

    @Test
    public void return1_when_ratingIs3() {
        // WHEN
        final int rating = placesRepository.getRating(3.);

        // THEN
        assertEquals(2, rating);
    }

    // -------------------------------------- PHOTO URL TESTS --------------------------------------

    @Test
    public void returnNull_when_photoReferenceIsNull() {
        // WHEN
        final String photoUrl = placesRepository.getPhotoUrl(null);

        // THEN
        assertNull(photoUrl);
    }

    @Test
    public void returnCompleteUrl_when_photoReferenceIsNotNull() {
        // WHEN
        final String photoUrl = placesRepository.getPhotoUrl("PHOTO_REFERENCE");

        // THEN
        assertEquals(
            "https://maps.googleapis.com/" +
                "maps/api/place/photo?" +
                "maxheight=720" +
                "&key=MAPS_API_KEY" +
                "&photoreference=PHOTO_REFERENCE",
            photoUrl
        );
    }

}