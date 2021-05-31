//package com.neige_i.go4lunch.view.map;
//
//import android.location.Location;
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//import androidx.lifecycle.MutableLiveData;
//
//import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
//import com.neige_i.go4lunch.domain.GetNearbyRestaurantsUseCase;
//import com.neige_i.go4lunch.domain.model.MapWrapper;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
//import static com.neige_i.go4lunch.view.map.MapViewModel.ZOOM_LEVEL_STREETS;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThrows;
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.mock;
//
//public class MapViewModelTest {
//
//    @Rule
//    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
//
//    // MarkerViewState fields
//    private final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";
//    private final String EXPECTED_NAME = "EXPECTED_NAME";
//    private final double EXPECTED_LAT = 1.0;
//    private final double EXPECTED_LNG = 2.0;
//    private final String EXPECTED_VICINITY = "EXPECTED_VICINITY";
//
//    // Values returned from mocked objects
//    private final MutableLiveData<MapWrapper> mapModel = new MutableLiveData<>();
//    private final double EXPECTED_MAP_LAT = 48.8566;
//    private final double EXPECTED_MAP_LNG = 2.3522;
//
//    // Mock objects
//    private final GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase = mock(GetNearbyRestaurantsUseCase.class);
//    private final Location currentLocation = mock(Location.class);
//
//    // ViewModel under test
//    private MapViewModel mapViewModel;
//
//    @Before
//    public void setUp() {
//        doReturn(mapModel).when(getNearbyRestaurantsUseCase).getNearby();
//        doReturn(EXPECTED_MAP_LAT).when(currentLocation).getLatitude();
//        doReturn(EXPECTED_MAP_LNG).when(currentLocation).getLongitude();
//
//        mapViewModel = new MapViewModel(getNearbyRestaurantsUseCase);
//    }
//
//    @Test
//    public void onMapAvailable_nominalCase() throws InterruptedException {
//        // Given
//        mapModel.setValue(new MapWrapper(
//            true,
//            currentLocation,
//            getDefaultResponse()
//        ));
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 2);
//
//        // Then
//        assertEquals(
//            new MapViewState(
//                true,
//                Arrays.asList(getDefaultMarkerViewState(1), getDefaultMarkerViewState(2)),
//                EXPECTED_MAP_LAT,
//                EXPECTED_MAP_LNG,
//                ZOOM_LEVEL_STREETS
//            ),
//            getOrAwaitValue(mapViewModel.getViewState())
//        );
//    }
//
//    @Test
//    public void onMapAvailable_altCase_initialBigZoom() throws InterruptedException {
//        // Given
//        mapModel.setValue(new MapWrapper(
//            true,
//            currentLocation,
//            getDefaultResponse()
//        ));
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 22);
//
//        // Then
//        assertEquals(
//            new MapViewState(
//                true,
//                Arrays.asList(getDefaultMarkerViewState(1), getDefaultMarkerViewState(2)),
//                EXPECTED_MAP_LAT,
//                EXPECTED_MAP_LNG,
//                22
//            ),
//            getOrAwaitValue(mapViewModel.getViewState())
//        );
//    }
//
//    @Test
//    public void onMapAvailable_edgeCase_responseWithoutResult() throws InterruptedException {
//        // Given
//        mapModel.setValue(new MapWrapper(
//            true,
//            currentLocation,
//            new NearbyResponse()
//        ));
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 2);
//
//        // Then
//        assertEquals(
//            new MapViewState(
//                true,
//                Collections.emptyList(),
//                EXPECTED_MAP_LAT,
//                EXPECTED_MAP_LNG,
//                ZOOM_LEVEL_STREETS
//            ),
//            getOrAwaitValue(mapViewModel.getViewState())
//        );
//    }
//
//    @Test
//    public void onMapAvailable_edgeCase_responseNull() throws InterruptedException {
//        // Given
//        mapModel.setValue(new MapWrapper(
//            true,
//            currentLocation,
//            null
//        ));
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 2);
//
//        // Then
//        assertEquals(
//            new MapViewState(
//                true,
//                Collections.emptyList(),
//                EXPECTED_MAP_LAT,
//                EXPECTED_MAP_LNG,
//                ZOOM_LEVEL_STREETS
//            ),
//            getOrAwaitValue(mapViewModel.getViewState())
//        );
//    }
//
//    @Test
//    public void onMapAvailable_edgeCase_noLocation() throws InterruptedException {
//        // Given
//        mapModel.setValue(new MapWrapper(
//            true,
//            null,
//            getDefaultResponse()
//        ));
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 2);
//
//        // Then
//        assertEquals(
//            new MapViewState(
//                true,
//                Arrays.asList(getDefaultMarkerViewState(1), getDefaultMarkerViewState(2)),
//                0,
//                0,
//                2
//            ),
//            getOrAwaitValue(mapViewModel.getViewState())
//        );
//    }
//
//    @Test
//    public void onMapAvailable_edgeCase_permissionDenied() throws InterruptedException {
//        // Given
//        mapModel.setValue(new MapWrapper(
//            false,
//            currentLocation,
//            getDefaultResponse()
//        ));
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 2);
//
//        // Then
//        assertEquals(
//            new MapViewState(
//                false,
//                Arrays.asList(getDefaultMarkerViewState(1), getDefaultMarkerViewState(2)),
//                EXPECTED_MAP_LAT,
//                EXPECTED_MAP_LNG,
//                ZOOM_LEVEL_STREETS
//            ),
//            getOrAwaitValue(mapViewModel.getViewState())
//        );
//    }
//
//    @Test
//    public void onMapAvailable_edgeCase_permissionDenied_noLocation_responseNull() throws InterruptedException {
//        // Given
//        mapModel.setValue(new MapWrapper(
//            false,
//            null,
//            null
//        ));
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 2);
//
//        // Then
//        assertEquals(
//            new MapViewState(
//                false,
//                Collections.emptyList(),
//                0,
//                0,
//                2
//            ),
//            getOrAwaitValue(mapViewModel.getViewState())
//        );
//    }
//
//    @Test
//    public void onMapAvailable_edge_case_noValueRetrieved() {
//        // Given: omit setValue
//
//        // When
//        mapViewModel.onMapAvailable(0, 0, 2);
//        final Throwable thrownException = assertThrows(
//            RuntimeException.class,
//            () -> getOrAwaitValue(mapViewModel.getViewState())
//        );
//
//        // Then
//        assertEquals("LiveData value was never set.", thrownException.getMessage());
//    }
//
//    // region IN
//
//    private NearbyResponse getDefaultResponse() {
//        final NearbyResponse nearbyResponse = new NearbyResponse();
//        nearbyResponse.setResults(Arrays.asList(getDefaultResult(1), getDefaultResult(2)));
//        return nearbyResponse;
//    }
//
//    private NearbyResponse.Result getDefaultResult(int index) {
//        final NearbyResponse.Result result = new NearbyResponse.Result();
//        final NearbyResponse.Geometry geometry = new NearbyResponse.Geometry();
//        final NearbyResponse.Location location = new NearbyResponse.Location();
//
//        result.setPlaceId(EXPECTED_PLACE_ID + index);
//        result.setName(EXPECTED_NAME + index);
//        location.setLat(EXPECTED_LAT + index);
//        location.setLng(EXPECTED_LNG + index);
//        result.setVicinity(EXPECTED_VICINITY + index);
//
//        geometry.setLocation(location);
//        result.setGeometry(geometry);
//
//        return result;
//    }
//
//    // endregion
//
//    // region OUT
//
//    private MarkerViewState getDefaultMarkerViewState(int index) {
//        return new MarkerViewState(
//            EXPECTED_PLACE_ID + index,
//            EXPECTED_NAME + index,
//            EXPECTED_LAT + index,
//            EXPECTED_LNG + index,
//            EXPECTED_VICINITY + index
//        );
//    }
//
//    // endregion
//}