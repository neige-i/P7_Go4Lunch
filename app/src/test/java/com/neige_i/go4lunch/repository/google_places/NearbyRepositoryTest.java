package com.neige_i.go4lunch.repository.google_places;

import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.PlacesApi;
import com.neige_i.go4lunch.repository.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.repository.google_places.model.RawNearbyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NearbyRepositoryTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final PlacesApi placesApiMock = mock(PlacesApi.class);
    @NonNull
    private final String mapsApiKey = "MAPS_API_KEY";

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private NearbyRepository nearbyRepository;

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location locationMock = mock(Location.class);

    // ------------------------------------------- CONST -------------------------------------------

    private static final double LATITUDE = 49.3833;
    private static final double LONGITUDE = 2.4;
    private static final double LAT = 100;
    private static final double LNG = 50;

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(LATITUDE).when(locationMock).getLatitude();
        doReturn(LONGITUDE).when(locationMock).getLongitude();

        // Init repository
        nearbyRepository = new NearbyRepository(placesApiMock, mapsApiKey);
    }

    // -------------------------------------- GET DATA TESTS ---------------------------------------

    @Test
    public void returnNull_when_getDate_with_nullQuery() {
        // WHEN
        final List<NearbyRestaurant> nearbyRestaurants = getValueForTesting(
            nearbyRepository.getData(null)
        );

        // THEN
        assertNull(nearbyRestaurants);
    }

    // --------------------------------------- REQUEST TESTS ---------------------------------------

    @Test
    public void getRestaurantByName_when_getAutocompleteRequest() {
        // WHEN
        nearbyRepository.getRequest(locationMock);

        // THEN
        verify(placesApiMock).getNearbyRestaurants(LATITUDE + "," + LONGITUDE);
        verifyNoMoreInteractions(placesApiMock);
    }

    // ------------------------------------- CLEAN DATA TESTS --------------------------------------

    @Test
    public void returnCorrectData_when_cleanData() {
        final String PLACE_ID = "PLACE_ID";
        final String NAME = "NAME";
        final String ADDRESS = "ADDRESS";

        // GIVEN
        final RawNearbyResponse rawNearbyResponse = new RawNearbyResponse(
            null,
            null,
            Arrays.asList(
                // Correct data with NonNull photo
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    getDefaultGeometry(0),
                    null,
                    NAME + 0,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 0,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 0,
                    null,
                    null
                ),
                // Correct data with empty photo
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    getDefaultGeometry(1),
                    null,
                    NAME + 1,
                    null,
                    Collections.emptyList(),
                    PLACE_ID + 1,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 1,
                    null,
                    null
                ),
                // Correct data with null photo
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    getDefaultGeometry(2),
                    null,
                    NAME + 2,
                    null,
                    null,
                    PLACE_ID + 2,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 2,
                    null,
                    null
                ),
                // Wrong data: null address
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    getDefaultGeometry(3),
                    null,
                    NAME + 3,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 3,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    null, // Address
                    null,
                    null
                ),
                // Wrong data: null name
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    getDefaultGeometry(4),
                    null,
                    null, // Name
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 4,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 4,
                    null,
                    null
                ),
                // Wrong data: null longitude
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    new RawNearbyResponse.Geometry(new RawNearbyResponse.Location(LAT, null), null),
                    null,
                    NAME + 5,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 5,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 5,
                    null,
                    null
                ),
                // Wrong data: null latitude
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    new RawNearbyResponse.Geometry(new RawNearbyResponse.Location(null, LNG), null),
                    null,
                    NAME + 6,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 6,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 6,
                    null,
                    null
                ),
                // Wrong data: null location
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    new RawNearbyResponse.Geometry(null, null),
                    null,
                    NAME + 7,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 7,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 7,
                    null,
                    null
                ),
                // Wrong data: null geometry
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    null, // Geometry
                    null,
                    NAME + 8,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 8,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 8,
                    null,
                    null
                ),
                // Wrong data: wrong business status
                new RawNearbyResponse.Result(
                    "wrong status",
                    getDefaultGeometry(9),
                    null,
                    NAME + 9,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 9,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 9,
                    null,
                    null
                ),
                // Wrong data: null business status
                new RawNearbyResponse.Result(
                    null, // Business status
                    getDefaultGeometry(10),
                    null,
                    NAME + 10,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    PLACE_ID + 10,
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 10,
                    null,
                    null
                ),
                // Wrong data: null place ID
                new RawNearbyResponse.Result(
                    "OPERATIONAL",
                    getDefaultGeometry(11),
                    null,
                    NAME + 11,
                    null,
                    Collections.singletonList(new RawNearbyResponse.Photo(null, null, "photo", null)),
                    null, // Place ID
                    null,
                    5.,
                    null,
                    null,
                    null,
                    null,
                    ADDRESS + 11,
                    null,
                    null
                )
            ),
            null
        );

        // WHEN
        final List<NearbyRestaurant> nearbyRestaurants =
            nearbyRepository.cleanDataFromRetrofit(rawNearbyResponse);

        // THEN
        assertEquals(
            // Only three data are correct
            Arrays.asList(
                new NearbyRestaurant(
                    PLACE_ID + 0,
                    NAME + 0,
                    ADDRESS + 0,
                    LAT + 0,
                    LNG + 0,
                    3,
                    "https://maps.googleapis.com/" +
                        "maps/api/place/photo?" +
                        "maxheight=720" +
                        "&key=" + mapsApiKey +
                        "&photoreference=" + "photo"
                ),
                new NearbyRestaurant(
                    PLACE_ID + 1,
                    NAME + 1,
                    ADDRESS + 1,
                    LAT + 1,
                    LNG + 1,
                    3,
                    null
                ),
                new NearbyRestaurant(
                    PLACE_ID + 2,
                    NAME + 2,
                    ADDRESS + 2,
                    LAT + 2,
                    LNG + 2,
                    3,
                    null
                )
            ),
            nearbyRestaurants
        );
    }

    @Test
    public void returnNullData_when_cleanData_with_nullResults() {
        // GIVEN
        final RawNearbyResponse rawNearbyResponse = new RawNearbyResponse(
            null,
            null,
            null, // Results
            null
        );

        // WHEN
        final List<NearbyRestaurant> autocompleteRestaurants =
            nearbyRepository.cleanDataFromRetrofit(rawNearbyResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullRawResponse() {
        // WHEN
        final List<NearbyRestaurant> nearbyRestaurants =
            nearbyRepository.cleanDataFromRetrofit(null);

        // THEN
        assertNull(nearbyRestaurants);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private RawNearbyResponse.Geometry getDefaultGeometry(int index) {
        return new RawNearbyResponse.Geometry(
            new RawNearbyResponse.Location(LAT + index, LNG + index),
            null
        );
    }
}