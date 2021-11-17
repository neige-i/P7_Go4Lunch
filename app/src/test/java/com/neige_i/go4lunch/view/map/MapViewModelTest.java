package com.neige_i.go4lunch.view.map;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.location.Location;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.domain.map.GetMapDataUseCase;
import com.neige_i.go4lunch.domain.map.MapData;
import com.neige_i.go4lunch.domain.map.RequestGpsUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetMapDataUseCase getMapDataUseCaseMock = mock(GetMapDataUseCase.class);
    private final RequestGpsUseCase requestGpsUseCaseMock = mock(RequestGpsUseCase.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private MapViewModel mapViewModel;

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location deviceLocation = mock(Location.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<MapData> mapDataMutableLiveData = new MutableLiveData<>();

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
        doReturn(mapDataMutableLiveData).when(getMapDataUseCaseMock).get();
        doReturn(DEVICE_LAT).when(deviceLocation).getLatitude();
        doReturn(DEVICE_LNG).when(deviceLocation).getLongitude();

        // Default behaviour
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocation,
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

        // Init ViewModel
        mapViewModel = new MapViewModel(getMapDataUseCaseMock, requestGpsUseCaseMock);

        // Retrieve initial map's CameraPosition when displayed for the first time
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEFAULT_LAT, DEFAULT_LNG),
            DEFAULT_ZOOM
        ));
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
        mapDataMutableLiveData.setValue(new MapData(
            false, // Denied location permission
            deviceLocation,
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

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
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocation,
            getDefaultRestaurantList(),
            false, // Disabled GPS
            getDefaultInterestedWorkmates()
        ));

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
        mapDataMutableLiveData.setValue(new MapData(
            true,
            null, // Unavailable location
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

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
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocation,
            Collections.emptyList(), // Unavailable restaurants
            true,
            getDefaultInterestedWorkmates()
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
                Collections.emptyList(), // No markers
                DEVICE_LAT,
                DEVICE_LNG,
                MapViewModel.DEFAULT_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_when_workmatesAreNotInterested() throws InterruptedException {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocation,
            getDefaultRestaurantList(),
            true,
            Collections.emptyMap() // No interested workmates
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
                Arrays.asList( // No interested workmate for all markers
                               getDefaultMarker(3, R.drawable.ic_marker_orange),
                               getDefaultMarker(2, R.drawable.ic_marker_orange),
                               getDefaultMarker(1, R.drawable.ic_marker_orange)
                ),
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
        // WHEN
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEVICE_LAT, DEVICE_LNG), // Same position as the current location
            MapViewModel.DEFAULT_ZOOM_LEVEL
        ));
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
    public void moveMapToLocation_when_locationButtonIsClicked_with_enabledGps_availableLocation() throws InterruptedException {
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
    public void doNotMoveMap_when_locationButtonIsClicked_with_enabledGps_unavailableLocation() throws InterruptedException {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            null, // Unavailable location
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        mapViewModel.onLocationButtonClicked();
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
    public void requestGps_when_locationButtonIsClicked_with_DisabledGps() throws InterruptedException {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocation,
            getDefaultRestaurantList(),
            false, // Disabled GPS
            getDefaultInterestedWorkmates()
        ));

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
        // WHEN
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEFAULT_LAT, DEFAULT_LNG),
            20 // Greater than DEFAULT_ZOOM_LEVEL
        ));
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
        // WHEN
        mapViewModel.onCameraMoved(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE); // Or REASON_API_ANIMATION
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
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocation,
            Collections.singletonList(getDefaultRestaurant(3)), // Add the restaurant #3 again
            true,
            getDefaultInterestedWorkmates()
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

    // --------------------------------------- REFRESH TESTS ---------------------------------------

    @Test
    public void refresh_when_fragmentIsResumed() {
        // WHEN
        mapViewModel.onFragmentResumed();

        // THEN
        verify(getMapDataUseCaseMock).get();
        verify(getMapDataUseCaseMock).refresh();
        verifyNoMoreInteractions(getMapDataUseCaseMock);
    }

    // ------------------------------------ SHOW DETAILS TESTS -------------------------------------

    @Test
    public void showDetails_when_infoWindowIsClicked_with_nonNullPlaceId() throws InterruptedException {
        // GIVEN
        final String expectedPlaceId = "Non-null place ID";

        // WHEN
        mapViewModel.onInfoWindowClick(expectedPlaceId);
        final String actualPlaceID = getOrAwaitValue(mapViewModel.getShowDetailsEvent());

        // THEN
        assertEquals(expectedPlaceId, actualPlaceID);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private List<NearbyRestaurant> getDefaultRestaurantList() {
        return Arrays.asList(getDefaultRestaurant(1), getDefaultRestaurant(2), getDefaultRestaurant(3));
    }

    @NonNull
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

    @NonNull
    private Map<String, Integer> getDefaultInterestedWorkmates() {
        return new HashMap<String, Integer>() {{
            put(EXPECTED_PLACE_ID + 1, 0);
            put(EXPECTED_PLACE_ID + 2, 1);
            put(EXPECTED_PLACE_ID + 3, 5);
        }};
    }

    @NonNull
    private List<MarkerViewState> getDefaultMarkerList() {
        return Arrays.asList(
            getDefaultMarker(3, R.drawable.ic_marker_green),
            getDefaultMarker(2, R.drawable.ic_marker_green),
            getDefaultMarker(1, R.drawable.ic_marker_orange)
        );
    }

    @NonNull
    private MarkerViewState getDefaultMarker(int index, @DrawableRes int markerDrawable) {
        return new MarkerViewState(
            EXPECTED_PLACE_ID + index,
            EXPECTED_NAME + index,
            EXPECTED_LAT + index,
            EXPECTED_LNG + index,
            EXPECTED_ADDRESS + index,
            markerDrawable
        );
    }
}