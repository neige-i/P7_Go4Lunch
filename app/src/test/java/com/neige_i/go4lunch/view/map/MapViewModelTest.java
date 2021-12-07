package com.neige_i.go4lunch.view.map;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
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

    private final Location deviceLocationMock = mock(Location.class);

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
    private static final CameraPosition DEFAULT_CAMERA_POSITION = CameraPosition.fromLatLngZoom(
        new LatLng(DEFAULT_LAT, DEFAULT_LNG),
        DEFAULT_ZOOM
    );

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(mapDataMutableLiveData).when(getMapDataUseCaseMock).get();
        doReturn(DEVICE_LAT).when(deviceLocationMock).getLatitude();
        doReturn(DEVICE_LNG).when(deviceLocationMock).getLongitude();

        // Init ViewModel
        mapViewModel = new MapViewModel(getMapDataUseCaseMock, requestGpsUseCaseMock);

        // Default behaviour
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocationMock,
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

        // Retrieve initial map's CameraPosition when displayed for the first time
        mapViewModel.onCameraStopped(DEFAULT_CAMERA_POSITION);
    }

    // ------------------------------------- DEPENDENCY TESTS --------------------------------------

    @Test
    public void returnViewState_when_getValue_with_defaultBehaviour() {
        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void returnViewState_when_getValue_with_deniedLocationPermission() {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            false, // Denied location permission
            deviceLocationMock,
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void returnViewState_when_getValue_with_disabledGps() {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocationMock,
            getDefaultRestaurantList(),
            false, // Disabled GPS
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void returnViewState_when_getValue_with_unavailableLocation() {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            null, // Unavailable location
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void returnViewState_when_getValue_with_sameLatitudeButDifferentLongitude() {
        // GIVEN
        doReturn(DEFAULT_LAT).when(deviceLocationMock).getLatitude();
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocationMock,
            getDefaultRestaurantList(),
            true,
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                DEFAULT_LAT, // Same latitude
                DEVICE_LNG,
                MapViewModel.DEFAULT_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    @Test
    public void returnViewState_when_getValue_with_unavailableRestaurants() {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocationMock,
            Collections.emptyList(), // Unavailable restaurants
            true,
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void returnViewState_when_getValue_with_noInterestedWorkmates() {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocationMock,
            getDefaultRestaurantList(),
            true,
            Collections.emptyMap() // No interested workmates
        ));

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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

    @Test
    public void doNothing_when_getValue_with_nullMapData() {
        // GIVEN
        mapDataMutableLiveData.setValue(null);

        // WHEN
        final int viewStateTrigger = getLiveDataTriggerCount(mapViewModel.getMapViewState());

        // THEN
        assertEquals(0, viewStateTrigger);
    }

    @Test
    public void doNothing_when_getValue_with_nullCameraPosition() {
        // GIVEN (Init ViewModel without calling onCameraStopped() afterwards == null CameraPosition)
        mapViewModel = new MapViewModel(getMapDataUseCaseMock, requestGpsUseCaseMock);

        // WHEN
        final int viewStateTrigger = getLiveDataTriggerCount(mapViewModel.getMapViewState());

        // THEN
        assertEquals(0, viewStateTrigger);
    }

    // ------------------------------ MAP CENTERED ON LOCATION TESTS -------------------------------

    @Test
    public void setButtonColorToBlue_when_stopCameraOnDeviceLocation() {
        // WHEN
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEVICE_LAT, DEVICE_LNG), // Same position as the current location
            MapViewModel.DEFAULT_ZOOM_LEVEL
        ));
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void moveMapToLocation_when_clickOnLocationButton_with_enabledGpsAndAvailableLocation() {
        // GIVEN (GPS is enabled in @Before)

        // WHEN
        mapViewModel.onLocationButtonClicked();
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void doNotMoveMap_when_clickOnLocationButton_with_enabledGpsAndUnavailableLocation() {
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
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void requestGps_when_clickOnLocationButton_with_disabledGps() {
        // GIVEN
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocationMock,
            getDefaultRestaurantList(),
            false, // Disabled GPS
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        mapViewModel.onLocationButtonClicked();
        getValueForTesting(mapViewModel.getMapViewState());

        // THEN
        verify(requestGpsUseCaseMock).request();
        verifyNoMoreInteractions(requestGpsUseCaseMock);
    }

    // ------------------------------------ MAXIMUM ZOOM TESTS -------------------------------------

    @Test
    public void keepCurrentZoomLevel_when_stopCameraWithHighZoomLevel() {
        // WHEN
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(
            new LatLng(DEFAULT_LAT, DEFAULT_LNG),
            20 // > DEFAULT_ZOOM_LEVEL
        ));
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void doNotMoveMap_when_getValue_with_mapCameraManuallyMoved() {
        // GIVEN
        mapViewModel.onCameraMoved(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE); // Or REASON_API_ANIMATION

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void doNotDuplicateMarker_when_getValue_with_sameNearbyRestaurantIsAdded() {
        // GIVEN
        getValueForTesting(mapViewModel.getMapViewState()); // Get the first state with default markers
        mapDataMutableLiveData.setValue(new MapData(
            true,
            deviceLocationMock,
            Collections.singletonList(getDefaultRestaurant(3)), // Add the restaurant #3 again
            true,
            getDefaultInterestedWorkmates()
        ));

        // WHEN
        final MapViewState mapViewState = getValueForTesting(mapViewModel.getMapViewState());

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
    public void refresh_when_resumeFragment() {
        // WHEN
        mapViewModel.onFragmentResumed();

        // THEN
        verify(getMapDataUseCaseMock).get();
        verify(getMapDataUseCaseMock).refresh();
        verifyNoMoreInteractions(getMapDataUseCaseMock);
    }

    // ------------------------------------ SHOW DETAILS TESTS -------------------------------------

    @Test
    public void showDetails_when_clickOnInfoWindow_with_nonNullPlaceId() {
        // WHEN
        mapViewModel.onInfoWindowClick("Non-null place ID");
        final String placeId = getValueForTesting(mapViewModel.getShowDetailsEvent());

        // THEN
        assertEquals("Non-null place ID", placeId);
    }

    @Test
    public void doNothing_when_clickOnInfoWindow_with_nullPlaceId() {
        // WHEN
        mapViewModel.onInfoWindowClick(null);
        final int showDetailTrigger = getLiveDataTriggerCount(mapViewModel.getShowDetailsEvent());

        // THEN
        assertEquals(0, showDetailTrigger);
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
            markerDrawable,
            size
        );
    }
}