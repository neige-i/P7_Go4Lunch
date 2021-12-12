package com.neige_i.go4lunch.data.google_places;

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

import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DetailsRepositoryTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final PlacesApi placesApiMock = mock(PlacesApi.class);
    @NonNull
    private final String mapsApiKey = "MAPS_API_KEY";

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private DetailsRepository detailsRepository;

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location locationMock = mock(Location.class);

    // ------------------------------------------- CONST -------------------------------------------

    private static final double LATITUDE = 49.3833;
    private static final double LONGITUDE = 2.4;
    private static final String PLACE_ID = "PLACE_ID";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(LATITUDE).when(locationMock).getLatitude();
        doReturn(LONGITUDE).when(locationMock).getLongitude();

        // Init repository
        detailsRepository = new DetailsRepository(placesApiMock, mapsApiKey);
    }

    // -------------------------------------- GET DATA TESTS ---------------------------------------

    @Test
    public void returnNull_when_getDate_with_nullQuery() {
        // WHEN
        final RestaurantDetails restaurantDetails = getValueForTesting(
            detailsRepository.getData(null)
        );

        // THEN
        assertNull(restaurantDetails);
    }

    // --------------------------------------- REQUEST TESTS ---------------------------------------

    @Test
    public void getRestaurantDetails_when_getDetailRequest() {
        // WHEN
        detailsRepository.getRequest(PLACE_ID);

        // THEN
        verify(placesApiMock).getRestaurantDetails(PLACE_ID);
        verifyNoMoreInteractions(placesApiMock);
    }

    // ------------------------------------- CLEAN DATA TESTS --------------------------------------

    @Test
    public void returnData_when_cleanData_with_nonNullPeriods() {
        // GIVEN
        final List<RawDetailsResponse.Period> periodList = Arrays.asList(
            // Correct periods
            new RawDetailsResponse.Period(new RawDetailsResponse.Close(1, "1500"), new RawDetailsResponse.Open(1, "0945")),
            // Wrong periods: null close/open
            new RawDetailsResponse.Period(null, null),
            // Wrong periods: null days
            new RawDetailsResponse.Period(new RawDetailsResponse.Close(null, "1500"), new RawDetailsResponse.Open(null, "0915")),
            // Wrong periods: null times
            new RawDetailsResponse.Period(new RawDetailsResponse.Close(1, null), new RawDetailsResponse.Open(1, null)),
            // Correct periods
            new RawDetailsResponse.Period(new RawDetailsResponse.Close(0, "2030"), new RawDetailsResponse.Open(0, "1330"))
        );
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                new RawDetailsResponse.OpeningHours(null, periodList, null), // NonNull periods
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertEquals(
            new RestaurantDetails(
                "ID",
                "name",
                "address",
                LATITUDE,
                LONGITUDE,
                3,
                null,
                "+33",
                "https://",
                Arrays.asList(
                    new RestaurantDetails.RestaurantHour(true, DayOfWeek.MONDAY, LocalTime.of(9, 45)),
                    new RestaurantDetails.RestaurantHour(false, DayOfWeek.MONDAY, LocalTime.of(15, 0)),
                    new RestaurantDetails.RestaurantHour(true, DayOfWeek.SUNDAY, LocalTime.of(13, 30)),
                    new RestaurantDetails.RestaurantHour(false, DayOfWeek.SUNDAY, LocalTime.of(20, 30))
                )
            ),
            autocompleteRestaurants
        );
    }

    @Test
    public void returnData_when_cleanData_with_nullPeriods() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                new RawDetailsResponse.OpeningHours(null, null, null), // Null periods
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertEquals(
            new RestaurantDetails(
                "ID",
                "name",
                "address",
                LATITUDE,
                LONGITUDE,
                3,
                null,
                "+33",
                "https://",
                Collections.emptyList()
            ),
            autocompleteRestaurants
        );
    }

    @Test
    public void returnData_when_cleanData_with_nonNullPhotos() {
        // GIVEN
        final RawDetailsResponse.Photo photo = new RawDetailsResponse.Photo(null, null, "photo", null);
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                null,
                Collections.singletonList(photo),
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertEquals(
            new RestaurantDetails(
                "ID",
                "name",
                "address",
                LATITUDE,
                LONGITUDE,
                3,
                "https://maps.googleapis.com/" +
                    "maps/api/place/photo?" +
                    "maxheight=720" +
                    "&key=" + mapsApiKey +
                    "&photoreference=" + "photo",
                "+33",
                "https://",
                Collections.emptyList()
            ),
            autocompleteRestaurants
        );
    }

    @Test
    public void returnData_when_cleanData_with_emptyPhotos() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                null,
                Collections.emptyList(), // Photo
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertEquals(
            new RestaurantDetails(
                "ID",
                "name",
                "address",
                LATITUDE,
                LONGITUDE,
                3,
                null,
                "+33",
                "https://",
                Collections.emptyList()
            ),
            autocompleteRestaurants
        );
    }

    @Test
    public void returnData_when_cleanData_with_nullPhotos() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                null,
                null, // Photo
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertEquals(
            new RestaurantDetails(
                "ID",
                "name",
                "address",
                LATITUDE,
                LONGITUDE,
                3,
                null,
                "+33",
                "https://",
                Collections.emptyList()
            ),
            autocompleteRestaurants
        );
    }

    @Test
    public void returnNullData_when_cleanData_with_nullAddress() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                null, // Address
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullName() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                getDefaultGeometry(),
                null, // Name
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullLongitude() {
        // GIVEN
        // Null longitude
        final RawDetailsResponse.Location location = new RawDetailsResponse.Location(LATITUDE, null);
        final RawDetailsResponse.Geometry geometry = new RawDetailsResponse.Geometry(location);
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                geometry,
                "name",
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullLatitude() {
        // GIVEN
        // Null latitude
        final RawDetailsResponse.Location location = new RawDetailsResponse.Location(null, LONGITUDE);
        final RawDetailsResponse.Geometry geometry = new RawDetailsResponse.Geometry(location);
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                geometry,
                "name",
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullLocation() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                new RawDetailsResponse.Geometry(null), // Null location
                "name",
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullGeometry() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                null, // Geometry
                "name",
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nonOperationalBusiness() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "wrong status", // Business status
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullBusinessStatus() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                null, // Business status
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                null,
                null,
                "ID",
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullPlaceID() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            new RawDetailsResponse.Result(
                "OPERATIONAL",
                "address",
                "+33",
                getDefaultGeometry(),
                "name",
                null,
                null,
                null,
                null, // Place ID
                5.,
                "https://"
            ),
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullPredictions() {
        // GIVEN
        final RawDetailsResponse rawDetailsResponse = new RawDetailsResponse(
            null,
            null,
            null
        );

        // WHEN
        final RestaurantDetails autocompleteRestaurants =
            detailsRepository.cleanDataFromRetrofit(rawDetailsResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullRawResponse() {
        // WHEN
        final RestaurantDetails restaurantDetails = detailsRepository.cleanDataFromRetrofit(null);

        // THEN
        assertNull(restaurantDetails);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private RawDetailsResponse.Geometry getDefaultGeometry() {
        return new RawDetailsResponse.Geometry(new RawDetailsResponse.Location(LATITUDE, LONGITUDE));
    }
}