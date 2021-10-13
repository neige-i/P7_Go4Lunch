package com.neige_i.go4lunch.view.map;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.domain.gps.GetGpsStatusUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationUseCase;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantsUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapViewModelTest {
    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private MapViewModel mapViewModel;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetLocationPermissionUseCase getLocationPermissionUseCaseMock = mock(GetLocationPermissionUseCase.class);
    private final GetLocationUseCase getLocationUseCaseMock = mock(GetLocationUseCase.class);
    private final GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCaseMock = mock(GetNearbyRestaurantsUseCase.class);
    private final GetGpsStatusUseCase getGpsStatusUseCaseMock = mock(GetGpsStatusUseCase.class);
    private final RequestGpsUseCase requestGpsUseCaseMock = mock(RequestGpsUseCase.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isGpsEnabledMutableLiveData = new MutableLiveData<>();
    private final Location deviceLocation = mock(Location.class);

    // --------------------------------- MARKER VIEW STATE FIELDS ----------------------------------

    private final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";
    private final String EXPECTED_NAME = "EXPECTED_NAME";
    private final double EXPECTED_LAT = 1.0;
    private final double EXPECTED_LNG = 2.0;
    private final String EXPECTED_ADDRESS = "EXPECTED_ADDRESS";

    // ------------------------------------------- CONST -------------------------------------------

    private static final double DEVICE_LAT = 48.8566;
    private static final double DEVICE_LNG = 2.3522;
    private static final double DEFAULT_LAT = 0;
    private static final double DEFAULT_LNG = 0;
    private static final float DEFAULT_ZOOM = 2;

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(true).when(getLocationPermissionUseCaseMock).isGranted();
        doReturn(locationMutableLiveData).when(getLocationUseCaseMock).get();
        doReturn(nearbyRestaurantsMutableLiveData).when(getNearbyRestaurantsUseCaseMock).get();
        doReturn(isGpsEnabledMutableLiveData).when(getGpsStatusUseCaseMock).isEnabled();
        doReturn(DEVICE_LAT).when(deviceLocation).getLatitude();
        doReturn(DEVICE_LNG).when(deviceLocation).getLongitude();

        // Default behaviour
        isGpsEnabledMutableLiveData.setValue(true);
        locationMutableLiveData.setValue(deviceLocation);
        nearbyRestaurantsMutableLiveData.setValue(getDefaultRestaurantList());

        // Init ViewModel
        mapViewModel = new MapViewModel(
            getLocationPermissionUseCaseMock,
            getLocationUseCaseMock,
            getNearbyRestaurantsUseCaseMock,
            getGpsStatusUseCaseMock,
            requestGpsUseCaseMock
        );

        // Retrieve initial map's CameraPosition when displayed for the first time
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEFAULT_LAT, DEFAULT_LNG),
            DEFAULT_ZOOM
        ));
        mapViewModel.onFragmentResumed();
    }

    // ------------------------------------- DEPENDENCY TESTS --------------------------------------

    @Test
    public void getDefaultMap() throws InterruptedException {
        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                DEVICE_LAT,
                DEVICE_LNG,
                MapViewModel.DEFAULT_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    @Test
    public void getEmptyMap_when_locationPermissionIsDenied() throws InterruptedException {
        // GIVEN
        doReturn(false).when(getLocationPermissionUseCaseMock).isGranted();
        mapViewModel.onFragmentResumed();

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                false, // No location layer
                false, // Hidden FAB
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                DEVICE_LAT,
                DEVICE_LNG,
                MapViewModel.DEFAULT_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_when_gpsIsDisabled() throws InterruptedException {
        // GIVEN
        isGpsEnabledMutableLiveData.setValue(false);

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                false, // No location layer
                true,
                R.drawable.ic_gps_off, // Off button
                android.R.color.holo_red_dark, // Red button
                getDefaultMarkerList(),
                DEFAULT_LAT, // No camera movement
                DEFAULT_LNG, // No camera movement
                DEFAULT_ZOOM // No camera movement
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_when_locationIsNotAvailable() throws InterruptedException {
        // GIVEN
        locationMutableLiveData.setValue(null);

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                android.R.color.holo_red_dark, // Red button
                getDefaultMarkerList(),
                DEFAULT_LAT, // No camera movement
                DEFAULT_LNG, // No camera movement
                DEFAULT_ZOOM // No camera movement
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_when_restaurantsAreNotAvailable() throws InterruptedException {
        // GIVEN
        nearbyRestaurantsMutableLiveData.setValue(null);

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                Collections.emptyList(), // No markers
                DEVICE_LAT,
                DEVICE_LNG,
                MapViewModel.DEFAULT_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    // ------------------------------ MAP CENTERED ON LOCATION TESTS -------------------------------

    @Test
    public void setButtonColorToBlue_when_mapCameraIsCenteredOnLocation() throws InterruptedException {
        // GIVEN
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEVICE_LAT, DEVICE_LNG), // Same position as the current location
            MapViewModel.DEFAULT_ZOOM_LEVEL
        ));

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.blue_google, // Color has changed
                getDefaultMarkerList(),
                DEVICE_LAT,
                DEVICE_LNG,
                MapViewModel.DEFAULT_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    // -------------------------------- LOCATION BUTTON CLICK TESTS --------------------------------

    @Test
    public void moveMapToLocation_when_locationButtonIsClickedAndGpsIsEnabled() throws InterruptedException {
        // GIVEN (GPS is enabled in @Before)

        // WHEN
        mapViewModel.onLocationButtonClicked();
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                DEVICE_LAT, // Move to coordinate
                DEVICE_LNG, // Move to coordinate
                MapViewModel.DEFAULT_ZOOM_LEVEL // Move to coordinate
            ),
            mapViewState
        );
    }

    @Test
    public void requestGps_when_locationButtonIsClickedAndGpsIsDisabled() throws InterruptedException {
        // GIVEN
        isGpsEnabledMutableLiveData.setValue(false);

        // WHEN
        mapViewModel.onLocationButtonClicked();
        getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        verify(requestGpsUseCaseMock).request();
        verifyNoMoreInteractions(requestGpsUseCaseMock);
    }

    // ------------------------------------ MAXIMUM ZOOM TESTS -------------------------------------

    @Test
    public void keepCurrentZoomLevel_when_mapZoomIsAboveDefaultLevel() throws InterruptedException {
        // GIVEN
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEFAULT_LAT, DEFAULT_LNG),
            20 // Greater than DEFAULT_ZOOM_LEVEL
        ));

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                DEVICE_LAT,
                DEVICE_LNG,
                20 // Zoom is unchanged
            ),
            mapViewState
        );
    }

    // ----------------------------- MAP NOT FOLLOWING LOCATION TESTS ------------------------------

    @Test
    public void doNotMoveMap_when_mapIsManuallyScrolled() throws InterruptedException {
        // GIVEN
        mapViewModel.onCameraMoved(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE); // Or REASON_API_ANIMATION

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                DEFAULT_LAT, // No camera movement
                DEFAULT_LNG, // No camera movement
                DEFAULT_ZOOM // No camera movement
            ),
            mapViewState
        );
    }

    // -------------------------------- NO DUPLICATE MARKERS TESTS ---------------------------------

    @Test
    public void doNotDuplicateMarker_when_sameNearbyRestaurantIsAdded() throws InterruptedException {
        // GIVEN
        getOrAwaitValue(mapViewModel.getMapViewState()); // Get the first state with default markers
        nearbyRestaurantsMutableLiveData.setValue(Collections.singletonList(
            getDefaultRestaurant(3) // Add the restaurant #3 again
        ));

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(), // The marker #3 is not duplicated
                DEVICE_LAT,
                DEVICE_LNG,
                MapViewModel.DEFAULT_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private List<NearbyRestaurant> getDefaultRestaurantList() {
        return Arrays.asList(getDefaultRestaurant(1), getDefaultRestaurant(2), getDefaultRestaurant(3));
    }

    private NearbyRestaurant getDefaultRestaurant(int index) {
        return new NearbyRestaurant(
            EXPECTED_PLACE_ID + index,
            EXPECTED_NAME + index,
            EXPECTED_ADDRESS + index,
            EXPECTED_LAT + index,
            EXPECTED_LNG + index,
            -1,
            null
        );
    }

    private List<MarkerViewState> getDefaultMarkerList() {
        return Arrays.asList(getDefaultMarker(1), getDefaultMarker(2), getDefaultMarker(3));
    }

    private MarkerViewState getDefaultMarker(int index) {
        return new MarkerViewState(
            EXPECTED_PLACE_ID + index,
            EXPECTED_NAME + index,
            EXPECTED_LAT + index,
            EXPECTED_LNG + index,
            EXPECTED_ADDRESS + index
        );
    }
}