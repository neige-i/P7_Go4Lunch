package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SetLocationPermissionUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private SetLocationPermissionUseCase setLocationPermissionUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationPermissionRepository locationPermissionRepositoryMock = mock(LocationPermissionRepository.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        setLocationPermissionUseCase = new SetLocationPermissionUseCaseImpl(locationPermissionRepositoryMock);
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_updatePermissionToTrue_when_permissionIsGranted() {
        // WHEN
        setLocationPermissionUseCase.set(true);

        // THEN
        verify(locationPermissionRepositoryMock).setLocationPermission(true);
        verifyNoMoreInteractions(locationPermissionRepositoryMock);
    }

    @Test
    public void verify_updatePermissionToFalse_when_permissionIsDenied() {
        // WHEN
        setLocationPermissionUseCase.set(false);

        // THEN
        verify(locationPermissionRepositoryMock).setLocationPermission(false);
        verifyNoMoreInteractions(locationPermissionRepositoryMock);
    }
}