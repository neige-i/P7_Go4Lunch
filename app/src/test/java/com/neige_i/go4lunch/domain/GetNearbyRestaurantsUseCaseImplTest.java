package com.neige_i.go4lunch.domain;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.domain.model.MapWrapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GetNearbyRestaurantsUseCaseImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Values returned from mocked objects
    private final MutableLiveData<Boolean> permissionGranted = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<NearbyResponse> nearbyResponse = new MutableLiveData<>();

    // Mock objects
    private final LocationRepository locationRepository = mock(LocationRepository.class);
    private final NearbyRepository nearbyRepository = mock(NearbyRepository.class);
    private final Location location = mock(Location.class);

    // UseCase under test
    private GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase;

    @Before
    public void setUp() {
        doReturn(permissionGranted).when(locationRepository).getLocationPermission();
        doReturn(currentLocation).when(locationRepository).getCurrentLocation();
        doReturn(nearbyResponse).when(nearbyRepository).getNearbyResponse(location);

        getNearbyRestaurantsUseCase = new GetNearbyRestaurantsUseCaseImpl(locationRepository, nearbyRepository);
    }

    @Test
    public void getNearby_nominalCase() throws InterruptedException {
        // Given
        permissionGranted.setValue(true);
        currentLocation.setValue(location);
        nearbyResponse.setValue(new NearbyResponse());

        // Then
        assertEquals(new MapWrapper(
            true,
            location,
            new NearbyResponse()
        ), getOrAwaitValue(getNearbyRestaurantsUseCase.getNearby()));
    }

    @Test
    public void getNearby_altCase_permissionDenied() throws InterruptedException {
        // Given
        permissionGranted.setValue(false);
        currentLocation.setValue(location);
        nearbyResponse.setValue(new NearbyResponse());

        // Then
        assertEquals(new MapWrapper(
            false,
            null,
            null
        ), getOrAwaitValue(getNearbyRestaurantsUseCase.getNearby()));

        verify(locationRepository, times(0)).getCurrentLocation();
        verify(nearbyRepository, times(0)).getNearbyResponse(location);
    }

    @Test
    public void getNearby_altCase_locationNull() throws InterruptedException {
        permissionGranted.setValue(true);
        currentLocation.setValue(null);
        nearbyResponse.setValue(new NearbyResponse());

        // Then
        assertEquals(new MapWrapper(
            true,
            null,
            null
        ), getOrAwaitValue(getNearbyRestaurantsUseCase.getNearby()));

        verify(nearbyRepository, times(0)).getNearbyResponse(location);
    }

    @Test
    public void getNearby_altCase_responseNull() throws InterruptedException {
        permissionGranted.setValue(true);
        currentLocation.setValue(location);
        nearbyResponse.setValue(null);

        // Then
        assertEquals(new MapWrapper(
            true,
            location,
            null
        ), getOrAwaitValue(getNearbyRestaurantsUseCase.getNearby()));
    }

    @Test
    public void getNearby_edgeCase_noPermissionRetrieved() {
        // omit setValue for permission
        currentLocation.setValue(location);
        nearbyResponse.setValue(new NearbyResponse());

        // When
        final Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> getOrAwaitValue(getNearbyRestaurantsUseCase.getNearby())
        );

        // Then
        assertEquals("LiveData value was never set.", thrownException.getMessage());

        verify(locationRepository, times(0)).getCurrentLocation();
        verify(nearbyRepository, times(0)).getNearbyResponse(location);
    }

    @Test
    public void getNearby_edgeCase_noLocationRetrieved() {
        permissionGranted.setValue(true);
        // omit setValue for permission
        nearbyResponse.setValue(new NearbyResponse());

        // When
        final Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> getOrAwaitValue(getNearbyRestaurantsUseCase.getNearby())
        );

        // Then
        assertEquals("LiveData value was never set.", thrownException.getMessage());

        verify(nearbyRepository, times(0)).getNearbyResponse(location);
    }

    @Test
    public void getNearby_edgeCase_noResponseRetrieved() {
        permissionGranted.setValue(true);
        currentLocation.setValue(location);
        // omit setValue for nearby response

        // When
        final Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> getOrAwaitValue(getNearbyRestaurantsUseCase.getNearby())
        );

        // Then
        assertEquals("LiveData value was never set.", thrownException.getMessage());
    }
}