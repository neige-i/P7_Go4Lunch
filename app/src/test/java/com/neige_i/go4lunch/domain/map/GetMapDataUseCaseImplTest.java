package com.neige_i.go4lunch.domain.map;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.data.google_places.AutocompleteRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.data.location.LocationPermissionRepository;
import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetMapDataUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationPermissionRepository locationPermissionRepositoryMock = mock(LocationPermissionRepository.class);
    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);
    private final NearbyRepository nearbyRepositoryMock = mock(NearbyRepository.class);
    private final GpsStateChangeReceiver gpsStateChangeReceiverMock = mock(GpsStateChangeReceiver.class);
    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final AutocompleteRepository autocompleteRepositoryMock = mock(AutocompleteRepository.class);

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location locationMock = mock(Location.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetMapDataUseCase getMapDataUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> gpsStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<AutocompleteRestaurant> autocompleteRestaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> interestedWorkmates1MutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> interestedWorkmates2MutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> interestedWorkmates3MutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- CONST -------------------------------------------

    private static final String PLACE_ID = "PLACE_ID";
    private static final String RESTAURANT_NAME = "RESTAURANT_NAME";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(true).when(locationPermissionRepositoryMock).isPermissionGranted();
        doReturn(locationMutableLiveData).when(locationRepositoryMock).getCurrentLocation();
        doReturn(nearbyRestaurantsMutableLiveData).when(nearbyRepositoryMock).getData(locationMock);
        doReturn(gpsStateMutableLiveData).when(gpsStateChangeReceiverMock).getGpsState();
        doReturn(autocompleteRestaurantMutableLiveData).when(autocompleteRepositoryMock).getCurrentSearchQuery();
        doReturn(interestedWorkmates1MutableLiveData).when(firestoreRepositoryMock).getWorkmatesEatingAt(PLACE_ID + 1);
        doReturn(interestedWorkmates2MutableLiveData).when(firestoreRepositoryMock).getWorkmatesEatingAt(PLACE_ID + 2);
        doReturn(interestedWorkmates3MutableLiveData).when(firestoreRepositoryMock).getWorkmatesEatingAt(PLACE_ID + 3);

        // Init UseCase
        getMapDataUseCase = new GetMapDataUseCaseImpl(
            locationPermissionRepositoryMock,
            locationRepositoryMock,
            nearbyRepositoryMock,
            gpsStateChangeReceiverMock,
            firestoreRepositoryMock,
            autocompleteRepositoryMock
        );

        // Default behaviour
        getMapDataUseCase.refresh();
        locationMutableLiveData.setValue(locationMock);
        nearbyRestaurantsMutableLiveData.setValue(getDefaultRestaurantList());
        gpsStateMutableLiveData.setValue(true);
        interestedWorkmates1MutableLiveData.setValue(Collections.emptyList());
        interestedWorkmates2MutableLiveData.setValue(Collections.nCopies(1, new User()));
        interestedWorkmates3MutableLiveData.setValue(Collections.nCopies(5, new User()));
    }

    // ------------------------------------- DEPENDENCY TESTS --------------------------------------

    @Test
    public void returnMapData_when_getValue_with_defaultBehaviour() {
        // WHEN
        final MapData mapData = getValueForTesting(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                locationMock,
                getDefaultMapRestaurantList(),
                true,
                getDefaultInterestedWorkmates()
            ),
            mapData
        );
    }

    @Test
    public void returnMapData_when_getValue_with_deniedLocationPermission() {
        // GIVEN
        doReturn(false).when(locationPermissionRepositoryMock).isPermissionGranted();
        getMapDataUseCase.refresh();

        // WHEN
        final MapData mapData = getValueForTesting(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                false, // Denied permission
                locationMock,
                getDefaultMapRestaurantList(),
                true,
                getDefaultInterestedWorkmates()
            ),
            mapData
        );
    }

    @Test
    public void returnMapData_when_getValue_with_unavailableLocation() {
        // GIVEN
        locationMutableLiveData.setValue(null);

        // WHEN
        final MapData mapData = getValueForTesting(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                null, // Unavailable location
                Collections.emptyList(), // Even nearby restaurant list is empty
                true,
                Collections.emptyMap() // If no restaurant, then no workmates
            ),
            mapData
        );
    }

    @Test
    public void returnMapData_when_getValue_with_unavailableNearbyRestaurants() {
        // GIVEN
        nearbyRestaurantsMutableLiveData.setValue(null);

        // WHEN
        final MapData mapData = getValueForTesting(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                locationMock,
                Collections.emptyList(), // Unavailable restaurants
                true,
                Collections.emptyMap() // If no restaurant, then no workmates
            ),
            mapData
        );
    }

    @Test
    public void returnMapData_when_getValue_with_disabledGps() {
        // GIVEN
        gpsStateMutableLiveData.setValue(false);

        // WHEN
        final MapData mapData = getValueForTesting(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                locationMock,
                getDefaultMapRestaurantList(),
                false, // Disabled GPS
                getDefaultInterestedWorkmates()
            ),
            mapData
        );
    }

    @Test
    public void returnMapData_when_getValue_with_searchQuery() {
        // GIVEN
        autocompleteRestaurantMutableLiveData.setValue(new AutocompleteRestaurant(
            PLACE_ID + 1,
            RESTAURANT_NAME + 3
        ));

        // WHEN
        final MapData mapData = getValueForTesting(getMapDataUseCase.get());

        // THEN
        assertEquals(
            new MapData(
                true,
                locationMock,
                Arrays.asList(
                    getDefaultMapRestaurant(1, true), // Is selected because same ID
                    getDefaultMapRestaurant(2, false),
                    getDefaultMapRestaurant(3, true) // Is selected because same name
                ),
                true,
                getDefaultInterestedWorkmates()
            ),
            mapData
        );
    }

    @Test
    public void doNothing_when_getValue_with_unavailableGps() {
        // GIVEN
        gpsStateMutableLiveData.setValue(null);

        // WHEN
        final int viewStateTrigger = getLiveDataTriggerCount(getMapDataUseCase.get());

        // THEN
        assertEquals(0, viewStateTrigger);
    }

    @Test
    public void doNothing_when_getValue_with_unavailableLocationPermission() {
        // GIVEN (Init UseCase without calling refresh() afterwards = null location permission)
        getMapDataUseCase = new GetMapDataUseCaseImpl(
            locationPermissionRepositoryMock,
            locationRepositoryMock,
            nearbyRepositoryMock,
            gpsStateChangeReceiverMock,
            firestoreRepositoryMock,
            autocompleteRepositoryMock
        );

        // WHEN
        final int viewStateTrigger = getLiveDataTriggerCount(getMapDataUseCase.get());

        // THEN
        assertEquals(0, viewStateTrigger);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private List<NearbyRestaurant> getDefaultRestaurantList() {
        return Arrays.asList(getDefaultRestaurant(1), getDefaultRestaurant(2), getDefaultRestaurant(3));
    }

    @NonNull
    private NearbyRestaurant getDefaultRestaurant(int index) {
        return new NearbyRestaurant(
            PLACE_ID + index,
            RESTAURANT_NAME + index,
            "address",
            index,
            index,
            -1,
            null
        );
    }

    @NonNull
    private Map<String, Integer> getDefaultInterestedWorkmates() {
        return new HashMap<String, Integer>() {{
            put(PLACE_ID + 1, 0);
            put(PLACE_ID + 2, 1);
            put(PLACE_ID + 3, 5);
        }};
    }

    @NonNull
    private List<MapRestaurant> getDefaultMapRestaurantList() {
        return Arrays.asList(
            getDefaultMapRestaurant(1, false),
            getDefaultMapRestaurant(2, false),
            getDefaultMapRestaurant(3, false)
        );
    }

    @NonNull
    private MapRestaurant getDefaultMapRestaurant(int index, boolean isSearched) {
        return new MapRestaurant(
            PLACE_ID + index,
            RESTAURANT_NAME + index,
            index,
            index,
            "address",
            isSearched
        );
    }
}