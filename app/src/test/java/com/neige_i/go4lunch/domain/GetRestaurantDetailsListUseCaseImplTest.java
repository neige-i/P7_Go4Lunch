package com.neige_i.go4lunch.domain;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.domain.model.ListModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GetRestaurantDetailsListUseCaseImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // MarkerViewState fields
    private final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";

    // Values returned from mocked objects
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<NearbyResponse> nearbyResponse = new MutableLiveData<>();
    private final MutableLiveData<DetailsResponse> detailsResponse1 = new MutableLiveData<>();
    private final MutableLiveData<DetailsResponse> detailsResponse2 = new MutableLiveData<>();

    // Mock objects
    private final LocationRepository locationRepository = mock(LocationRepository.class);
    private final NearbyRepository nearbyRepository = mock(NearbyRepository.class);
    private final DetailsRepository detailsRepository = mock(DetailsRepository.class);
    private final Location location = mock(Location.class);

    // UseCase under test
    private GetRestaurantDetailsListUseCase getRestaurantDetailsListUseCase;

    @Before
    public void setUp() {
        doReturn(currentLocation).when(locationRepository).getCurrentLocation();
        doReturn(nearbyResponse).when(nearbyRepository).getNearbyResponse(location);
        doReturn(detailsResponse1).when(detailsRepository).getDetailsResponse(eq(EXPECTED_PLACE_ID+1));
        doReturn(detailsResponse2).when(detailsRepository).getDetailsResponse(eq(EXPECTED_PLACE_ID+2));

        getRestaurantDetailsListUseCase = new GetRestaurantDetailsListUseCaseImpl(locationRepository, nearbyRepository, detailsRepository);
    }

    @Test
    public void getDetailsList_nominalCase() throws InterruptedException {
        // Given
        currentLocation.setValue(location);
        nearbyResponse.setValue(getDefaultNearbyResponse());
        detailsResponse1.setValue(getDefaultDetailsResponse(1));
        detailsResponse2.setValue(getDefaultDetailsResponse(2));

        // Then
        assertEquals(new ListModel(
            Arrays.asList(getDefaultDetailsResponse(1), getDefaultDetailsResponse(2)),
            location
        ), getOrAwaitValue(getRestaurantDetailsListUseCase.getDetailsList()));
    }

    // region IN

    private NearbyResponse getDefaultNearbyResponse() {
        final NearbyResponse nearbyResponse = new NearbyResponse();
        nearbyResponse.setResults(Arrays.asList(getDefaultResult(1), getDefaultResult(2)));
        return nearbyResponse;
    }

    private NearbyResponse.Result getDefaultResult(int index) {
        final NearbyResponse.Result result = new NearbyResponse.Result();
        result.setPlaceId(EXPECTED_PLACE_ID + index);
        return result;
    }

    private DetailsResponse getDefaultDetailsResponse(int index) {
        final DetailsResponse detailsResponse = new DetailsResponse();
        final DetailsResponse.Result result = new DetailsResponse.Result();

        result.setPlaceId(EXPECTED_PLACE_ID + index);
        detailsResponse.setResult(result);

        return detailsResponse;
    }

    // endregion
}