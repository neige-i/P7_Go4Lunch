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
import com.neige_i.go4lunch.repository.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.repository.google_places.model.RawAutocompleteResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("unchecked")
public class AutocompleteRepositoryTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final PlacesApi placesApiMock = mock(PlacesApi.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private AutocompleteRepository autocompleteRepository;

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location locationMock = mock(Location.class);
    private final Call<RawAutocompleteResponse> rawAutocompleteCallMock = mock(Call.class);
    private final Response<RawAutocompleteResponse> rawAutocompleteResponse = mock(Response.class);

    // ------------------------------------- ARGUMENT CAPTORS --------------------------------------

    private final ArgumentCaptor<Callback<RawAutocompleteResponse>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

    // ------------------------------------------- CONST -------------------------------------------

    private static final double LATITUDE = 49.3833;
    private static final double LONGITUDE = 2.4;
    private static final String QUERY = "QUERY";
    private static final String PLACE_ID = "PLACE_ID";
    private static final String DESCRIPTION = "DESCRIPTION";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(LATITUDE).when(locationMock).getLatitude();
        doReturn(LONGITUDE).when(locationMock).getLongitude();
        doReturn(rawAutocompleteCallMock).when(placesApiMock).getRestaurantsByName(QUERY, LATITUDE + "," + LONGITUDE);
        doReturn(new RawAutocompleteResponse(
            Collections.singletonList(new RawAutocompleteResponse.Prediction(
                DESCRIPTION + 0, null, PLACE_ID + 0, null, null, null, Collections.singletonList("restaurant")
            )),
            null
        )).when(rawAutocompleteResponse).body();

        // Init repository
        autocompleteRepository = new AutocompleteRepository(placesApiMock, "MAPS_API_KEY");
    }

    // -------------------------------------- GET DATA TESTS ---------------------------------------

    @Test
    public void returnDataFromApiAndCache_when_getDateMultipleTimes() {
        // GIVEN
        getValueForTesting(
            autocompleteRepository.getData(new RawAutocompleteQuery(QUERY, locationMock)) // Get data once
        );

        verify(rawAutocompleteCallMock).enqueue(callbackCaptor.capture());
        callbackCaptor.getValue().onResponse(mock(Call.class), rawAutocompleteResponse);

        // WHEN
        final List<AutocompleteRestaurant> autocompleteRestaurants = getValueForTesting(
            autocompleteRepository.getData(new RawAutocompleteQuery(QUERY, locationMock)) // Get data twice
        );

        // THEN
        assertEquals(
            Collections.singletonList(
                new AutocompleteRestaurant(PLACE_ID + 0, DESCRIPTION + 0)
            ),
            autocompleteRestaurants
        );
        verify(placesApiMock).getRestaurantsByName(QUERY, LATITUDE + "," + LONGITUDE); // Is called only once
        verifyNoMoreInteractions(placesApiMock);
    }

    @Test
    public void returnNull_when_getDate_with_nullQuery() {
        // WHEN
        final List<AutocompleteRestaurant> autocompleteRestaurants = getValueForTesting(
            autocompleteRepository.getData(null)
        );

        // THEN
        assertNull(autocompleteRestaurants);
    }

    // --------------------------------------- REQUEST TESTS ---------------------------------------

    @Test
    public void getRestaurantByName_when_getAutocompleteRequest() {
        // WHEN
        autocompleteRepository.getRequest(new RawAutocompleteQuery(
            QUERY,
            locationMock
        ));

        // THEN
        verify(placesApiMock).getRestaurantsByName(QUERY, LATITUDE + "," + LONGITUDE);
        verifyNoMoreInteractions(placesApiMock);
    }

    // ------------------------------------- CLEAN DATA TESTS --------------------------------------

    @Test
    public void returnCorrectData_when_cleanData() {
        // GIVEN
        final RawAutocompleteResponse rawAutocompleteResponse = new RawAutocompleteResponse(
            Arrays.asList(
                // Correct data
                new RawAutocompleteResponse.Prediction(
                    DESCRIPTION + 0, null, PLACE_ID + 0, null, null, null, Collections.singletonList("restaurant")
                ),
                // Wrong data: no description
                new RawAutocompleteResponse.Prediction(
                    null, null, PLACE_ID + 1, null, null, null, Collections.singletonList("restaurant")
                ),
                // Wrong data: no place ID
                new RawAutocompleteResponse.Prediction(
                    DESCRIPTION + 2, null, null, null, null, null, Collections.singletonList("restaurant")
                ),
                // Wrong data: no types
                new RawAutocompleteResponse.Prediction(
                    DESCRIPTION + 3, null, PLACE_ID + 3, null, null, null, null
                ),
                // Wrong data: not a restaurant
                new RawAutocompleteResponse.Prediction(
                    DESCRIPTION + 4, null, PLACE_ID + 4, null, null, null, Collections.singletonList("office")
                )
            ),
            null
        );

        // WHEN
        final List<AutocompleteRestaurant> autocompleteRestaurants =
            autocompleteRepository.cleanDataFromRetrofit(rawAutocompleteResponse);

        // THEN
        assertEquals(
            // Only one data is correct
            Collections.singletonList(
                new AutocompleteRestaurant(PLACE_ID + 0, DESCRIPTION + 0)
            ),
            autocompleteRestaurants
        );
    }

    @Test
    public void returnNullData_when_cleanData_with_nullPredictions() {
        // GIVEN
        final RawAutocompleteResponse rawAutocompleteResponse = new RawAutocompleteResponse(
            null, // Predictions
            null
        );

        // WHEN
        final List<AutocompleteRestaurant> autocompleteRestaurants =
            autocompleteRepository.cleanDataFromRetrofit(rawAutocompleteResponse);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    @Test
    public void returnNullData_when_cleanData_with_nullRawResponse() {
        // WHEN
        final List<AutocompleteRestaurant> autocompleteRestaurants =
            autocompleteRepository.cleanDataFromRetrofit(null);

        // THEN
        assertNull(autocompleteRestaurants);
    }

    // ------------------------------------ SEARCH QUERY TESTS -------------------------------------

    @Test
    public void updateCurrentSearch_when_setSearch() {
        // GIVEN
        final AutocompleteRestaurant expectedAutocompleteRestaurant = new AutocompleteRestaurant(
            "PLACE_ID",
            "RESTAURANT_NAME"
        );

        // WHEN
        autocompleteRepository.setCurrentSearch(expectedAutocompleteRestaurant);
        final AutocompleteRestaurant actualAutocompleteRestaurant =
            getValueForTesting(autocompleteRepository.getCurrentSearchQuery());

        // THEN
        assertEquals(expectedAutocompleteRestaurant, actualAutocompleteRestaurant);
    }
}