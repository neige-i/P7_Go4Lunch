package com.neige_i.go4lunch.data.google_places;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RawNearbyResponse;

import org.junit.Test;

import java.util.List;

public class PlacesRepositoryTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final String mapsApiKey = "MAPS_API_KEY";

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final PlacesRepository<Location, RawNearbyResponse, List<NearbyRestaurant>> placesRepository = new NearbyRepository(
        mock(PlacesApi.class),
        mapsApiKey
    );

    // --------------------------------------- ADDRESS TESTS ---------------------------------------

    @Test
    public void returnShortAddress_when_getAddressWithCommaInside() {
        // WHEN
        final String address = placesRepository.getAddress("221B Baker Street, London, UK");

        // THEN
        assertEquals("221B Baker Street", address);
    }

    @Test
    public void returnCompleteAddress_when_getAddressWithoutCommaInside() {
        // WHEN
        final String address = placesRepository.getAddress("Main street");

        // THEN
        assertEquals("Main street", address);
    }

    // --------------------------------------- RATING TESTS ----------------------------------------

    @Test
    public void returnNegativeInt_when_getNullRating() {
        // WHEN
        final int rating = placesRepository.getRating(null);

        // THEN
        assertEquals(-1, rating);
    }

    @Test
    public void return0_when_getRating1() {
        // WHEN
        final int rating = placesRepository.getRating(1.);

        // THEN
        assertEquals(0, rating);
    }

    @Test
    public void return3_when_getRating5() {
        // WHEN
        final int rating = placesRepository.getRating(5.);

        // THEN
        assertEquals(3, rating);
    }

    @Test
    public void return2_when_getRating3() {
        // WHEN
        final int rating = placesRepository.getRating(3.);

        // THEN
        assertEquals(2, rating);
    }

    // -------------------------------------- PHOTO URL TESTS --------------------------------------

    @Test
    public void returnNull_when_getPhotoWithNullReference() {
        // WHEN
        final String photoUrl = placesRepository.getPhotoUrl(null);

        // THEN
        assertNull(photoUrl);
    }

    @Test
    public void returnCompleteUrl_when_getPhotoWithNonNullReference() {
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