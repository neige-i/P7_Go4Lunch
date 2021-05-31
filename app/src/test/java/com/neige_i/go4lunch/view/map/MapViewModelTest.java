package com.neige_i.go4lunch.view.map;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.domain.location.GetGpsStatusUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationUseCase;
import com.neige_i.go4lunch.domain.location.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.place_nearby.GetNearbyRestaurantsUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
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

    private final MutableLiveData<Boolean> isLocationPermissionGrantedMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isGpsEnabledMutableLiveData = new MutableLiveData<>();
    private final Location deviceLocation = mock(Location.class);
    private final double DEVICE_LAT = 48.8566;
    private final double DEVICE_LNG = 2.3522;

    // --------------------------------- MARKER VIEW STATE FIELDS ----------------------------------

    private final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";
    private final String EXPECTED_NAME = "EXPECTED_NAME";
    private final double EXPECTED_LAT = 1.0;
    private final double EXPECTED_LNG = 2.0;
    private final String EXPECTED_ADDRESS = "EXPECTED_ADDRESS";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() throws InterruptedException {
        doReturn(isLocationPermissionGrantedMutableLiveData).when(getLocationPermissionUseCaseMock).isGranted();
        doReturn(currentLocationMutableLiveData).when(getLocationUseCaseMock).get();
        doReturn(nearbyRestaurantsMutableLiveData).when(getNearbyRestaurantsUseCaseMock).get();
        doReturn(isGpsEnabledMutableLiveData).when(getGpsStatusUseCaseMock).isEnabled();
        doReturn(DEVICE_LAT).when(deviceLocation).getLatitude();
        doReturn(DEVICE_LNG).when(deviceLocation).getLongitude();

        // Default behaviour (granted location permission, enabled gps, available location & restaurants
        isLocationPermissionGrantedMutableLiveData.setValue(true);
        isGpsEnabledMutableLiveData.setValue(true);
        currentLocationMutableLiveData.setValue(deviceLocation);
        nearbyRestaurantsMutableLiveData.setValue(getDefaultRestaurantList());

        mapViewModel = new MapViewModel(getLocationPermissionUseCaseMock, getLocationUseCaseMock, getNearbyRestaurantsUseCaseMock, getGpsStatusUseCaseMock, requestGpsUseCaseMock);
    }

    // --------------------------------------- GENERAL TESTS ---------------------------------------

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
                MapViewModel.STREET_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_withoutLocationLayer_withoutButton_when_locationPermissionIsDenied() throws InterruptedException {
        // GIVEN
        isLocationPermissionGrantedMutableLiveData.setValue(false); // Denied location permission

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
                MapViewModel.STREET_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_withoutLocationLayer_withRedOffButton_withoutMapCoordinates_when_gpsIsDisabled() throws InterruptedException {
        // GIVEN
        isGpsEnabledMutableLiveData.setValue(false); // Disabled gps

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
                null, // No map coordinates
                null, // No map coordinates
                null // No map coordinates
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_withoutMapCoordinates_when_locationIsNotAvailable() throws InterruptedException {
        // GIVEN
        currentLocationMutableLiveData.setValue(null); // Unavailable location

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
                null, // No map coordinates
                null, // No map coordinates
                null // No map coordinates
            ),
            mapViewState
        );
    }

    @Test
    public void getMap_withoutMarkers_when_restaurantsAreNotAvailable() throws InterruptedException {
        // GIVEN
        nearbyRestaurantsMutableLiveData.setValue(null); // Unavailable restaurants

        // WHEN
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                null, // No markers
                DEVICE_LAT,
                DEVICE_LNG,
                MapViewModel.STREET_ZOOM_LEVEL
            ),
            mapViewState
        );
    }

    // -------------------------------------- FAB STYLE TESTS --------------------------------------

    @Test
    public void setButtonColorToBlue_when_mapCameraIsCenteredOnLocation() throws InterruptedException {
        // WHEN (map is centered on location)
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(new LatLng(DEVICE_LAT, DEVICE_LNG), -1));
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.blue_google, // Color has changed
                null,
                null,
                null,
                null
            ),
            mapViewState
        );
    }

    // ---------------------------------- FAB CLICK ACTION TESTS -----------------------------------

    @Test
    public void moveMapToLocation_when_fabIsClickedAndGpsIsEnabled() throws InterruptedException {
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
                null,
                DEVICE_LAT, // Move to coordinate
                DEVICE_LNG, // Move to coordinate
                MapViewModel.STREET_ZOOM_LEVEL // Move to coordinate
            ),
            mapViewState
        );
    }

    @Test
    public void requestGps_when_fabIsClickedAndGpsIsDisabled() throws InterruptedException {
        // GIVEN
        isGpsEnabledMutableLiveData.setValue(false); // Disabled gps

        // WHEN
        mapViewModel.onLocationButtonClicked();
        getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        verify(requestGpsUseCaseMock).request();
    }

    // ------------------------------------ MAXIMUM ZOOM TESTS -------------------------------------

    @Test
    public void zoomToStreetLevel_when_zoomIsBelowStreetLevel() throws InterruptedException {
        // GIVEN (map is not centered on location, with a low zoom)
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(new LatLng(0, 0), 0));

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
                null,
                DEVICE_LAT, // From device location
                DEVICE_LNG, // From device location
                MapViewModel.STREET_ZOOM_LEVEL // Default zoom level
            ),
            mapViewState
        );
    }

    @Test
    public void keepCurrentZoomLevel_when_zoomIsAboveStreetLevel() throws InterruptedException {
//        getOrAwaitValue(mapViewModel.getMapViewState());
        // GIVEN (map is not centered on location, with a big zoom)
        mapViewModel.onCameraStopped(CameraPosition.fromLatLngZoom(new LatLng(0, 0), 18));

        // WHEN
        mapViewModel.onLocationButtonClicked();
//        currentLocationMutableLiveData.setValue(deviceLocation);
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                DEVICE_LAT, // From device location
                DEVICE_LNG, // From device location
                18f // From CameraPosition
            ),
            mapViewState
        );
    }

    // ----------------------------- MAP NOT FOLLOWING LOCATION TESTS ------------------------------

    @Test
    public void doNotSetMapCoordinates_when_userHasManuallyScrolledTheMap() throws InterruptedException {
        // GIVEN
        getOrAwaitValue(mapViewModel.getMapViewState());
        mapViewModel.setCameraMovedManually(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE); // Or REASON_API_ANIMATION

        // WHEN
        currentLocationMutableLiveData.setValue(deviceLocation);
        final MapViewState mapViewState = getOrAwaitValue(mapViewModel.getMapViewState());

        // THEN
        assertEquals(
            new MapViewState(
                true,
                true,
                R.drawable.ic_gps_on,
                R.color.black,
                getDefaultMarkerList(),
                null, // No map coordinates
                null, // No map coordinates
                null // No map coordinates
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