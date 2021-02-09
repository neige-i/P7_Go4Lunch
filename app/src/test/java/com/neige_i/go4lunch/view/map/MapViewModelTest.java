package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.LocationRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static com.neige_i.go4lunch.view.map.MapViewModel.ZOOM_LEVEL_STREETS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Initial states
    private final int NORMAL_STATE = 0;
    private final int EDGE_CASE_RESPONSE_RESULT_NULL = 1;
    private final int EDGE_CASE_RESPONSE_NULL = 2;
    private final int EDGE_CASE_RESPONSE_UNAVAILABLE = 3;
    private final int EDGE_CASE_LOCATION_UNAVAILABLE = 4;
    private final int EDGE_CASE_PERMISSION_DENIED = 5;
    private final int EDGE_CASE_PERMISSION_UNAVAILABLE = 6;
    private final int NORMAL_STATE_BIG_ZOOM = 7;

    // MarkerViewState fields
    private final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";
    private final String EXPECTED_NAME = "EXPECTED_NAME";
    private final double EXPECTED_LAT = 1.0;
    private final double EXPECTED_LNG = 2.0;
    private final String EXPECTED_VICINITY = "EXPECTED_VICINITY";

    // MapViewState fields
    private final double EXPECTED_MAP_LAT = 48.8566;
    private final double EXPECTED_MAP_LNG = 2.3522;
    private final double INITIAL_MAP_LAT = 0.;
    private final double INITIAL_MAP_LNG = 0.;
    private final float INITIAL_MAP_ZOOM = 2f;
    private final float BIG_MAP_ZOOM = 22f;

    // Values returned from mocked repositories
    private final MutableLiveData<Location> location = new MutableLiveData<>();
    private final MutableLiveData<Boolean> permission = new MutableLiveData<>();
    private final MutableLiveData<NearbyResponse> nearbyResponse = new MutableLiveData<>();

    // Mock objects
    // ASKME: difference between mock() and @Mock
    private final Location currentLocation = mock(Location.class);
    private final LocationRepository locationRepository = mock(LocationRepository.class);
    private final NearbyRepository nearbyRepository = mock(NearbyRepository.class);

    // ViewModel under test
    private MapViewModel mapViewModel;

    @Before
    public void setUp() {
        doReturn(permission).when(locationRepository).isLocationPermissionGranted();
        doReturn(location).when(locationRepository).getCurrentLocation();
        doReturn(nearbyResponse).when(nearbyRepository).getPlacesResponse(currentLocation);
        doReturn(EXPECTED_MAP_LAT).when(currentLocation).getLatitude();
        doReturn(EXPECTED_MAP_LNG).when(currentLocation).getLongitude();

        mapViewModel = new MapViewModel(nearbyRepository, locationRepository);
    }

    @Test
    public void onMapAvailable_nominalCase() throws InterruptedException {
        // Given
        setInitialState(NORMAL_STATE);

        // When
        setMapWithInitialZoom(true);

        // Then
        assertEquals(
            getExpectedViewState(NORMAL_STATE),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    @Test
    public void onMapAvailable_edgeCase_responseWithoutResult() throws InterruptedException {
        // Given
        setInitialState(EDGE_CASE_RESPONSE_RESULT_NULL);

        // When
        setMapWithInitialZoom(true);

        // Then
        assertEquals(
            getExpectedViewState(EDGE_CASE_RESPONSE_RESULT_NULL),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    @Test
    public void onMapAvailable_edgeCase_responseNull() throws InterruptedException {
        // Given
        setInitialState(EDGE_CASE_RESPONSE_NULL);

        // When
        setMapWithInitialZoom(true);

        // Then
        assertEquals(
            getExpectedViewState(EDGE_CASE_RESPONSE_NULL),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    @Test
    public void onMapAvailable_edgeCase_noResponseTriggered() throws InterruptedException {
        // Given
        setInitialState(EDGE_CASE_RESPONSE_UNAVAILABLE);

        // When
        setMapWithInitialZoom(true);

        // Then
        assertEquals(
            getExpectedViewState(EDGE_CASE_RESPONSE_UNAVAILABLE),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    @Test
    public void onMapAvailable_edgeCase_noLocationTriggered() throws InterruptedException {
        // Given
        setInitialState(EDGE_CASE_LOCATION_UNAVAILABLE);

        // When
        setMapWithInitialZoom(true);

        // Then
        assertEquals(
            getExpectedViewState(EDGE_CASE_LOCATION_UNAVAILABLE),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    @Test
    public void onMapAvailable_edgeCase_permissionDenied() throws InterruptedException {
        // Given
        setInitialState(EDGE_CASE_PERMISSION_DENIED);

        // When
        setMapWithInitialZoom(true);

        // Then
        assertEquals(
            getExpectedViewState(EDGE_CASE_PERMISSION_DENIED),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    @Test
    public void onMapAvailable_edgeCase_noPermissionTriggered() {
        // Given
        setInitialState(EDGE_CASE_PERMISSION_UNAVAILABLE);

        // When
        setMapWithInitialZoom(true);
        Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> getOrAwaitValue(mapViewModel.getViewState())
        );

        // Then
        assertEquals("LiveData value was never set.", thrownException.getMessage());
    }

    @Test
    public void onCameraCentered_initialSmallZoom() throws InterruptedException {
        // Given
        setInitialState(NORMAL_STATE);
        // ASKME: mandatory call to onMapAvailable()
        setMapWithInitialZoom(true);

        // When: map must be centered
        mapViewModel.onCameraCentered();

        // Then
        assertEquals(
            getExpectedViewState(NORMAL_STATE),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    @Test
    public void onCameraCentered_initialBigZoom() throws InterruptedException {
        // Given
        setInitialState(NORMAL_STATE_BIG_ZOOM);
        setMapWithInitialZoom(false);

        // When: map must be centered
        mapViewModel.onCameraCentered();

        // Then
        assertEquals(
            getExpectedViewState(NORMAL_STATE_BIG_ZOOM),
            getOrAwaitValue(mapViewModel.getViewState())
        );
    }

    // region IN

    /**
     * Initializes the value of the {@link MutableLiveData} returned by the mocked repositories.
     */
    private void setInitialState(int whichCase) {
        switch (whichCase) {
            case EDGE_CASE_PERMISSION_UNAVAILABLE:
                break;
            case EDGE_CASE_PERMISSION_DENIED:
                permission.setValue(false);
                break;
            default:
                permission.setValue(true);
        }

        // ASKME: should test impossible scenario or not (commented lines)
        //  logic: no permission (unavailable/denied) -> no location -> no response

        switch (whichCase) {
            case EDGE_CASE_LOCATION_UNAVAILABLE:
//            case EDGE_CASE_PERMISSION_UNAVAILABLE:
//            case EDGE_CASE_PERMISSION_DENIED:
                break;
            default:
                location.setValue(currentLocation);
        }

        switch (whichCase) {
            case EDGE_CASE_RESPONSE_UNAVAILABLE:
//            case EDGE_CASE_LOCATION_UNAVAILABLE:
//            case EDGE_CASE_PERMISSION_UNAVAILABLE:
            case EDGE_CASE_PERMISSION_DENIED: // If this is commented, should add condition in code to make test pass
                break;
            case EDGE_CASE_RESPONSE_NULL:
                nearbyResponse.setValue(null);
                break;
            case EDGE_CASE_RESPONSE_RESULT_NULL:
                nearbyResponse.setValue(new NearbyResponse());
                break;
            default:
                final NearbyResponse dummyResponse = new NearbyResponse();
                dummyResponse.setResults(Arrays.asList(getDefaultResult(1), getDefaultResult(2)));
                nearbyResponse.setValue(dummyResponse);
        }
    }

    private NearbyResponse.Result getDefaultResult(int index) {
        final NearbyResponse.Result result = new NearbyResponse.Result();
        final NearbyResponse.Geometry geometry = new NearbyResponse.Geometry();
        final NearbyResponse.Location location = new NearbyResponse.Location();

        result.setPlaceId(EXPECTED_PLACE_ID + index);
        result.setName(EXPECTED_NAME + index);
        location.setLat(EXPECTED_LAT + index);
        location.setLng(EXPECTED_LNG + index);
        result.setVicinity(EXPECTED_VICINITY + index);

        geometry.setLocation(location);
        result.setGeometry(geometry);

        return result;
    }

    private void setMapWithInitialZoom(boolean isInitialZoom) {
        mapViewModel.onMapAvailable(
            INITIAL_MAP_LAT,
            INITIAL_MAP_LNG,
            isInitialZoom ? INITIAL_MAP_ZOOM : BIG_MAP_ZOOM
        );
    }
    // endregion

    // region OUT

    /**
     * Returns the expected view state except in the {@link #EDGE_CASE_PERMISSION_UNAVAILABLE} case
     * when no view state is returned.
     *
     * @see #onMapAvailable_edgeCase_noLocationTriggered()
     */
    private MapViewState getExpectedViewState(int whichCase) {
        final boolean isPermissionGranted = whichCase != EDGE_CASE_PERMISSION_DENIED;

        final List<MarkerViewState> markerViewStates =
            whichCase == NORMAL_STATE || whichCase == NORMAL_STATE_BIG_ZOOM
                ? Arrays.asList(getDefaultMarkerViewState(1), getDefaultMarkerViewState(2))
                : new ArrayList<>();

        final double mapLatitude;
        final double mapLongitude;
        final float mapZoom;
        switch (whichCase) {
            case EDGE_CASE_LOCATION_UNAVAILABLE:
            case EDGE_CASE_PERMISSION_DENIED:
                mapLatitude = INITIAL_MAP_LAT;
                mapLongitude = INITIAL_MAP_LNG;
                mapZoom = INITIAL_MAP_ZOOM;
                break;
            case NORMAL_STATE_BIG_ZOOM:
                mapLatitude = EXPECTED_MAP_LAT;
                mapLongitude = EXPECTED_MAP_LNG;
                mapZoom = BIG_MAP_ZOOM;
                break;
            default:
                mapLatitude = EXPECTED_MAP_LAT;
                mapLongitude = EXPECTED_MAP_LNG;
                mapZoom = ZOOM_LEVEL_STREETS;
        }

        return new MapViewState(
            isPermissionGranted,
            markerViewStates,
            mapLatitude,
            mapLongitude,
            mapZoom
        );
    }

    private MarkerViewState getDefaultMarkerViewState(int index) {
        return new MarkerViewState(
            EXPECTED_PLACE_ID + index,
            EXPECTED_NAME + index,
            EXPECTED_LAT + index,
            EXPECTED_LNG + index,
            EXPECTED_VICINITY + index
        );
    }
    // endregion
}