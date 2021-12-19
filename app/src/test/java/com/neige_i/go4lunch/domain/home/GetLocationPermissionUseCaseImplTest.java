package com.neige_i.go4lunch.domain.home;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.repository.location.LocationPermissionRepository;

import org.junit.Rule;
import org.junit.Test;

public class GetLocationPermissionUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationPermissionRepository locationPermissionRepositoryMock = mock(LocationPermissionRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final GetLocationPermissionUseCase getLocationPermissionUseCase =
        new GetLocationPermissionUseCaseImpl(locationPermissionRepositoryMock);

    // --------------------------------- LOCATION PERMISSION TESTS ---------------------------------

    @Test
    public void returnTrue_when_getLocationPermission_with_granted() {
        // GIVEN
        doReturn(true).when(locationPermissionRepositoryMock).isPermissionGranted();

        // WHEN
        final boolean isPermissionGranted = getLocationPermissionUseCase.isGranted();

        // THEN
        assertTrue(isPermissionGranted);
    }

    @Test
    public void returnFalse_when_getLocationPermission_with_denied() {
        // GIVEN
        doReturn(false).when(locationPermissionRepositoryMock).isPermissionGranted();

        // WHEN
        final boolean isPermissionGranted = getLocationPermissionUseCase.isGranted();

        // THEN
        assertFalse(isPermissionGranted);
    }
}