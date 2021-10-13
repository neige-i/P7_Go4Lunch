package com.neige_i.go4lunch.domain.google_places;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class GetNearbyRestaurantsUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository mockLocationRepository = mock(LocationRepository.class);
    private final NearbyRepository mockNearbyRepository = mock(NearbyRepository.class);

    private final Location location = mock(Location.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Location> currentLocationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(currentLocationMutableLiveData).when(mockLocationRepository).getCurrentLocation();
        doReturn(nearbyRestaurantsMutableLiveData).when(mockNearbyRepository).getNearbyRestaurants(location);

        // Init UseCase
        getNearbyRestaurantsUseCase = new GetNearbyRestaurantsUseCaseImpl(mockLocationRepository, mockNearbyRepository);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnNearbyRestaurantList_when_nominalCase() throws InterruptedException {
        // GIVEN
        currentLocationMutableLiveData.setValue(location);
        nearbyRestaurantsMutableLiveData.setValue(getDefaultNearbyRestaurantList());

        // WHEN
        final List<NearbyRestaurant> nearbyRestaurants = getOrAwaitValue(getNearbyRestaurantsUseCase.get());

        // THEN
        assertEquals(getDefaultNearbyRestaurantList(), nearbyRestaurants);
    }

    @Test
    public void returnNull_when_nearbyRestaurantListIsNull() throws InterruptedException {
        // GIVEN
        currentLocationMutableLiveData.setValue(location);
        nearbyRestaurantsMutableLiveData.setValue(null);

        // WHEN
        final List<NearbyRestaurant> nearbyRestaurants = getOrAwaitValue(getNearbyRestaurantsUseCase.get());

        // THEN
        assertNull(nearbyRestaurants);
    }

    // ------------------------------------------- UTIL --------------------------------------------

    /**
     * Returns a list with a single {@link NearbyRestaurant} item, initialized with arbitrary values.
     */
    private List<NearbyRestaurant> getDefaultNearbyRestaurantList() {
        return Collections.singletonList(new NearbyRestaurant("", "", "", 0, 0, 0, null));
    }
}