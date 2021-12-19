package com.neige_i.go4lunch.repository.google_places;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.PlacesApi;

import org.junit.Test;

import retrofit2.Call;

public class PlacesRepositoryTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final String mapsApiKey = "MAPS_API_KEY";

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final PlacesRepository<Void, Void, Void> placesRepository =
        new PlacesRepository<Void, Void, Void>(mock(PlacesApi.class), mapsApiKey) {
            @SuppressWarnings("ConstantConditions")
            @NonNull
            @Override
            Call<Void> getRequest(@NonNull Void unused) {
                return null;
            }

            @Nullable
            @Override
            Void cleanDataFromRetrofit(@Nullable Void unused) {
                return null;
            }
        };

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location locationMock = mock(Location.class);

    // ----------------------------------- LOCATION STRING TESTS -----------------------------------

    @Test
    public void returnString_when_getLocationString() {
        // GIVEN
        doReturn(49.3761).when(locationMock).getLatitude();
        doReturn(2.4142).when(locationMock).getLongitude();

        // WHEN
        final String locationString = placesRepository.getLocationString(locationMock);

        // THEN
        assertEquals("49.3761,2.4142", locationString);
    }

    // --------------------------------------- ADDRESS TESTS ---------------------------------------

    @Test
    public void returnShortAddress_when_getAddress_with_commaInside() {
        // WHEN
        final String address = placesRepository.getAddress("221B Baker Street, London, UK");

        // THEN
        assertEquals("221B Baker Street", address);
    }

    @Test
    public void returnCompleteAddress_when_getAddress_with_noCommaInside() {
        // WHEN
        final String address = placesRepository.getAddress("Main street");

        // THEN
        assertEquals("Main street", address);
    }

    // --------------------------------------- RATING TESTS ----------------------------------------

    @Test
    public void returnNegativeInt_when_getRating_withNullValue() {
        // WHEN
        final int rating = placesRepository.getRating(null);

        // THEN
        assertEquals(-1, rating);
    }

    @Test
    public void return0_when_getRating_with_value1() {
        // WHEN
        final int rating = placesRepository.getRating(1.);

        // THEN
        assertEquals(0, rating);
    }

    @Test
    public void return3_when_getRating_with_value5() {
        // WHEN
        final int rating = placesRepository.getRating(5.);

        // THEN
        assertEquals(3, rating);
    }

    @Test
    public void return2_when_getRating_with_value3() {
        // WHEN
        final int rating = placesRepository.getRating(3.);

        // THEN
        assertEquals(2, rating);
    }

    // -------------------------------------- PHOTO URL TESTS --------------------------------------

    @Test
    public void returnNull_when_getPhoto_with_nullReference() {
        // WHEN
        final String photoUrl = placesRepository.getPhotoUrl(null);

        // THEN
        assertNull(photoUrl);
    }

    @Test
    public void returnCompleteUrl_when_getPhoto_with_nonNullReference() {
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