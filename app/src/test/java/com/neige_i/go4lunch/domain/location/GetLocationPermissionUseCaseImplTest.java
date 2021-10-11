package com.neige_i.go4lunch.domain.location;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class GetLocationPermissionUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetLocationPermissionUseCase getLocationPermissionUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationPermissionRepository locationPermissionRepositoryMock = mock(LocationPermissionRepository.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        getLocationPermissionUseCase = new GetLocationPermissionUseCaseImpl(locationPermissionRepositoryMock);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnTrue_when_permissionIsGranted() {
        // GIVEN
        doReturn(true).when(locationPermissionRepositoryMock).isPermissionGranted();

        // WHEN
        final boolean isPermissionGranted = getLocationPermissionUseCase.isGranted();

        // THEN
        assertTrue(isPermissionGranted);
    }

    @Test
    public void returnFalse_when_permissionIsDenied() {
        // GIVEN
        doReturn(false).when(locationPermissionRepositoryMock).isPermissionGranted();

        // WHEN
        final boolean isPermissionGranted = getLocationPermissionUseCase.isGranted();

        // THEN
        assertFalse(isPermissionGranted);
    }
}