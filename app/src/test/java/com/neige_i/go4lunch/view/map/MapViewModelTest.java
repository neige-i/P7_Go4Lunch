package com.neige_i.go4lunch.view.map;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.PlacesRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;


public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";
    private final String EXPECTED_NAME = "EXPECTED_NAME";

    private final MutableLiveData<NearbyResponse> nearbyResponseMutableLiveData = new MutableLiveData<>();

    private final PlacesRepository placesRepository = Mockito.mock(PlacesRepository.class);

    private MapViewModel mapViewModel;

    @Before
    public void setUp() {
        Mockito.doReturn(nearbyResponseMutableLiveData).when(placesRepository).getNearbyRestaurants();

        mapViewModel = new MapViewModel(placesRepository);
    }

    @Test
    public void nominal_case() throws InterruptedException {
        // Given
        nearbyResponseMutableLiveData.setValue(
            getDefaultNearbyResponse(
                getDefaultResult(1),
                getDefaultResult(2)
            )
        );

        // When
        List<MapViewState> result = getOrAwaitValue(mapViewModel.getMapViewStateLiveData());

        // Then
        assertEquals(
            getDefaultMapViewStates(
                getDefaultMapViewState(1),
                getDefaultMapViewState(2)
            ),
            result
        );
    }

    @Test
    public void edge_case_no_result_available() throws InterruptedException {
        // Given
        nearbyResponseMutableLiveData.setValue(getDefaultNearbyResponse());

        // When
        List<MapViewState> result = getOrAwaitValue(mapViewModel.getMapViewStateLiveData());

        // Then
        assertEquals(
            getDefaultMapViewStates(),
            result
        );
    }

    // region IN
    private NearbyResponse getDefaultNearbyResponse(NearbyResponse.Result... expectedResults) {
        NearbyResponse nearbyResponse = new NearbyResponse();
        List<NearbyResponse.Result> results = new ArrayList<>(Arrays.asList(expectedResults));

        nearbyResponse.setResults(results);

        return nearbyResponse;
    }

    private NearbyResponse.Result getDefaultResult(int index) {
        NearbyResponse.Result result = new NearbyResponse.Result();

        result.setPlaceId(EXPECTED_PLACE_ID + index);
        result.setName(EXPECTED_NAME + index);

        return result;
    }
    // endregion

    // region OUT
    private List<MapViewState> getDefaultMapViewStates(MapViewState... expectedMapViewStates) {
        return new ArrayList<>(Arrays.asList(expectedMapViewStates));
    }

    private MapViewState getDefaultMapViewState(int index) {
        return new MapViewState(
            EXPECTED_PLACE_ID + index,
            EXPECTED_NAME + index
        );
    }
    // endregion
}