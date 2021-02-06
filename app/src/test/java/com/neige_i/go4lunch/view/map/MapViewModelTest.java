package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.BaseRepository;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // MarkerViewState fields
    private final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";
    private final String EXPECTED_NAME = "EXPECTED_NAME";
    private final double EXPECTED_LAT = 1.0;
    private final double EXPECTED_LNG = 2.0;
    private final String EXPECTED_VICINITY = "EXPECTED_VICINITY";

    // MapViewState fields
    private final boolean EXPECTED_LOC_PERM = true;
    private final double EXPECTED_MAP_LAT = 48.8566;
    private final double EXPECTED_MAP_LNG = 2.3522;
    private final float EXPECTED_MAP_ZOOM = 15f;

    // Values returned from repositories
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NearbyResponse> nearbyResponseMutableLiveData = new MutableLiveData<>();

    // Repositories injected into ViewModel
    private final Location location = mock(Location.class);
    private final LocationRepository locationRepository = mock(LocationRepository.class);
    private final BaseRepository nearbyRepository = mock(NearbyRepository.class);

    // ViewModel under test
    private MapViewModel mapViewModel;

    @Before
    public void setUp() {
        doReturn(locationPermissionMutableLiveData).when(locationRepository).isLocationPermissionGranted();
        doReturn(locationMutableLiveData).when(locationRepository).getCurrentLocation();
        doReturn(nearbyResponseMutableLiveData).when(nearbyRepository).executeDetailsRequest(location);
        doReturn(EXPECTED_MAP_LAT).when(location).getLatitude();
        doReturn(EXPECTED_MAP_LNG).when(location).getLongitude();

        mapViewModel = new MapViewModel(nearbyRepository, locationRepository);
    }

    @Test
    public void nominal_case() throws InterruptedException {
        // Given
        mapViewModel.onMapAvailable(0.0, 0.0);
        locationPermissionMutableLiveData.setValue(EXPECTED_LOC_PERM);
        locationMutableLiveData.setValue(location);
        nearbyResponseMutableLiveData.setValue(
            getDefaultNearbyResponse(
                getDefaultResult(1),
                getDefaultResult(2)
            )
        );

        // When
        final MapViewState result = getOrAwaitValue(mapViewModel.getViewState());

        // Then
        assertEquals(
            getDefaultMapViewStates(
                getDefaultMarkerViewState(1),
                getDefaultMarkerViewState(2)
            ),
            result
        );
    }

    @Test
    public void edge_case_no_result_available() throws InterruptedException {
        // Given
        mapViewModel.onMapAvailable(0.0, 0.0);
        locationPermissionMutableLiveData.setValue(EXPECTED_LOC_PERM);
        locationMutableLiveData.setValue(location);
        nearbyResponseMutableLiveData.setValue(getDefaultNearbyResponse());

        // When
        final MapViewState result = getOrAwaitValue(mapViewModel.getViewState());

        // Then
        assertEquals(
            getDefaultMapViewStates(),
            result
        );
    }

    // region IN
    private NearbyResponse getDefaultNearbyResponse(NearbyResponse.Result... expectedResults) {
        final NearbyResponse nearbyResponse = new NearbyResponse();
        final List<NearbyResponse.Result> results = new ArrayList<>(Arrays.asList(expectedResults));

        nearbyResponse.setResults(results);

        return nearbyResponse;
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
    // endregion

    // region OUT
    private MapViewState getDefaultMapViewStates(MarkerViewState... markerViewStates) {
        return new MapViewState(
            EXPECTED_LOC_PERM,
            new ArrayList<>(Arrays.asList(markerViewStates)),
            EXPECTED_MAP_LAT,
            EXPECTED_MAP_LNG,
            EXPECTED_MAP_ZOOM
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