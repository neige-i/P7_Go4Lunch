package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;
import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class GetLocationPermissionUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetLocationPermissionUseCase getLocationPermissionUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationPermissionRepository mockLocationPermissionRepository = mock(LocationPermissionRepository.class);
    private final LocationRepository mockLocationRepository = mock(LocationRepository.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(locationPermissionMutableLiveData).when(mockLocationPermissionRepository).getLocationPermission();

        // Init UseCase
        getLocationPermissionUseCase = new GetLocationPermissionUseCaseImpl(mockLocationPermissionRepository, mockLocationRepository);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void startLocationUpdates_when_permissionIsGranted() throws InterruptedException {
        // GIVEN
        locationPermissionMutableLiveData.setValue(true);

        // WHEN
        final boolean isPermissionGranted = getOrAwaitValue(getLocationPermissionUseCase.isPermissionGranted());

        // THEN
        assertTrue(isPermissionGranted);

        verify(mockLocationRepository).startLocationUpdates();
        verifyNoMoreInteractions(mockLocationRepository);
    }

    @Test
    public void removeLocationUpdates_when_permissionIsDenied() throws InterruptedException {
        // GIVEN
        locationPermissionMutableLiveData.setValue(false);

        // WHEN
        final boolean isPermissionGranted = getOrAwaitValue(getLocationPermissionUseCase.isPermissionGranted());

        // THEN
        assertFalse(isPermissionGranted);

        verify(mockLocationRepository).removeLocationUpdates();
        verifyNoMoreInteractions(mockLocationRepository);
    }
}