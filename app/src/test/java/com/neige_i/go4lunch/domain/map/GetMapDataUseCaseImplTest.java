package com.neige_i.go4lunch.domain.map;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.data.location.LocationPermissionRepository;
import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetMapDataUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationPermissionRepository locationPermissionRepositoryMock = mock(LocationPermissionRepository.class);
    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);
    private final NearbyRepository nearbyRepositoryMock = mock(NearbyRepository.class);
    private final GpsStateChangeReceiver gpsStateChangeReceiverMock = mock(GpsStateChangeReceiver.class);

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location locationMock = mock(Location.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetMapDataUseCase getMapDataUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> gpsStateMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(true).when(locationPermissionRepositoryMock).isPermissionGranted();
        doReturn(locationMutableLiveData).when(locationRepositoryMock).getCurrentLocation();
        doReturn(nearbyRestaurantsMutableLiveData).when(nearbyRepositoryMock).getNearbyRestaurants(locationMock);
        doReturn(gpsStateMutableLiveData).when(gpsStateChangeReceiverMock).getGpsState();

        // Init UseCase
        getMapDataUseCase = new GetMapDataUseCaseImpl(
            locationPermissionRepositoryMock,
            locationRepositoryMock,
            nearbyRepositoryMock,
            gpsStateChangeReceiverMock
        );

        // Default behaviour
        getMapDataUseCase.refresh();
        locationMutableLiveData.setValue(locationMock);
        nearbyRestaurantsMutableLiveData.setValue(getDefaultRestaurantList());
        gpsStateMutableLiveData.setValue(true);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void getDefaultMapData() throws InterruptedException {
        // WHEN
        final MapData mapData = getOrAwaitValue(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                locationMock,
                getDefaultRestaurantList(),
                true
            ),
            mapData
        );
    }

    @Test
    public void getMapData_when_locationPermissionIsDenied() throws InterruptedException {
        // GIVEN
        doReturn(false).when(locationPermissionRepositoryMock).isPermissionGranted();
        getMapDataUseCase.refresh();

        // WHEN
        final MapData mapData = getOrAwaitValue(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                false,
                locationMock,
                getDefaultRestaurantList(),
                true
            ),
            mapData
        );
    }

    @Test
    public void getMapData_when_locationIsNotAvailable() throws InterruptedException {
        // GIVEN
        locationMutableLiveData.setValue(null);

        // WHEN
        final MapData mapData = getOrAwaitValue(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                null,
                Collections.emptyList(), // Even nearby restaurant list is empty
                true
            ),
            mapData
        );
    }

    @Test
    public void getMapData_when_nearbyRestaurantsAreNotAvailable() throws InterruptedException {
        // GIVEN
        nearbyRestaurantsMutableLiveData.setValue(null);

        // WHEN
        final MapData mapData = getOrAwaitValue(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                locationMock,
                Collections.emptyList(),
                true
            ),
            mapData
        );
    }

    @Test
    public void getMapData_when_gpsIsDisabled() throws InterruptedException {
        // GIVEN
        gpsStateMutableLiveData.setValue(false);

        // WHEN
        final MapData mapData = getOrAwaitValue(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                locationMock,
                getDefaultRestaurantList(),
                false
            ),
            mapData
        );
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private List<NearbyRestaurant> getDefaultRestaurantList() {
        return Arrays.asList(getDefaultRestaurant(1), getDefaultRestaurant(2), getDefaultRestaurant(3));
    }

    private NearbyRestaurant getDefaultRestaurant(int index) {
        return new NearbyRestaurant(
            "EXPECTED_PLACE_ID" + index,
            "EXPECTED_NAME" + index,
            "EXPECTED_ADDRESS" + index,
            index,
            index,
            -1,
            null
        );
    }
}