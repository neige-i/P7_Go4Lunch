package com.neige_i.go4lunch.domain.home;

import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.repository.google_places.AutocompleteRepository;
import com.neige_i.go4lunch.repository.google_places.RawAutocompleteQuery;
import com.neige_i.go4lunch.repository.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.repository.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GetAutocompleteResultsUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);
    private final AutocompleteRepository autocompleteRepositoryMock = mock(AutocompleteRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetAutocompleteResultsUseCase getAutocompleteResultsUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AutocompleteRestaurant>> autocompleteRestaurantsMutableLiveData = new MutableLiveData<>();

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location locationMock = mock(Location.class);

    // ------------------------------------------- CONST -------------------------------------------

    private static final String QUERY = "QUERY";
    private static final String PLACE_ID = "PLACE_ID";
    private static final String RESTAURANT_NAME = "RESTAURANT_NAME";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(locationMutableLiveData).when(locationRepositoryMock).getCurrentLocation();
        doReturn(autocompleteRestaurantsMutableLiveData).when(autocompleteRepositoryMock)
            .getData(new RawAutocompleteQuery(QUERY, locationMock));

        // Init UseCase
        getAutocompleteResultsUseCase = new GetAutocompleteResultsUseCaseImpl(
            locationRepositoryMock,
            autocompleteRepositoryMock
        );

        // Default behaviour
        locationMutableLiveData.setValue(locationMock);
        autocompleteRestaurantsMutableLiveData.setValue(Arrays.asList(
            getDefaultAutocompleteRestaurant(0),
            getDefaultAutocompleteRestaurant(1),
            getDefaultAutocompleteRestaurant(2)
        ));
    }

    // ----------------------------------- SHOW GPS DIALOG TESTS -----------------------------------

    @Test
    public void returnAutocompleteResults_when_getValue() {
        // WHEN
        final List<AutocompleteRestaurant> autocompleteRestaurants =
            getValueForTesting(getAutocompleteResultsUseCase.get(QUERY));

        // THEN
        assertEquals(
            Arrays.asList(
                getDefaultAutocompleteRestaurant(0),
                getDefaultAutocompleteRestaurant(1),
                getDefaultAutocompleteRestaurant(2)
            ),
            autocompleteRestaurants
        );
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private AutocompleteRestaurant getDefaultAutocompleteRestaurant(int index) {
        return new AutocompleteRestaurant(
            PLACE_ID + index,
            RESTAURANT_NAME + index
        );
    }
}