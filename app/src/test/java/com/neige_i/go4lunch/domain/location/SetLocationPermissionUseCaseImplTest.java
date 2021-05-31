package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.location.LocationPermissionRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SetLocationPermissionUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private SetLocationPermissionUseCase setLocationPermissionUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationPermissionRepository mockLocationPermissionRepository = mock(LocationPermissionRepository.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> locationPermissionMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(locationPermissionMutableLiveData).when(mockLocationPermissionRepository).getLocationPermission();

        // Init UseCase
        setLocationPermissionUseCase = new SetLocationPermissionUseCaseImpl(mockLocationPermissionRepository);
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_grantLocationPermission_when_currentPermissionIsDenied() {
        // GIVEN
        locationPermissionMutableLiveData.setValue(false);

        // WHEN
        setLocationPermissionUseCase.setPermission(true);

        // THEN
        verify(mockLocationPermissionRepository).getLocationPermission();
        verify(mockLocationPermissionRepository).updateLocationPermission(true);
        verifyNoMoreInteractions(mockLocationPermissionRepository);
    }

    @Test
    public void verify_grantLocationPermission_when_currentPermissionIsNull() {
        // GIVEN
        locationPermissionMutableLiveData.setValue(null);

        // WHEN
        setLocationPermissionUseCase.setPermission(true);

        // THEN
        verify(mockLocationPermissionRepository).getLocationPermission();
        verify(mockLocationPermissionRepository).updateLocationPermission(true);
        verifyNoMoreInteractions(mockLocationPermissionRepository);
    }

    @Test
    public void verify_doNotGrantLocationPermission_when_currentPermissionIsAlreadyGranted() {
        // GIVEN
        locationPermissionMutableLiveData.setValue(true);

        // WHEN
        setLocationPermissionUseCase.setPermission(true);

        // THEN
        verify(mockLocationPermissionRepository).getLocationPermission();
        verify(mockLocationPermissionRepository, never()).updateLocationPermission(true);
        verifyNoMoreInteractions(mockLocationPermissionRepository);
    }

    @Test
    public void verify_denyLocationPermission_when_currentPermissionIsGranted() {
        // GIVEN
        locationPermissionMutableLiveData.setValue(true);

        // WHEN
        setLocationPermissionUseCase.setPermission(false);

        // THEN
        verify(mockLocationPermissionRepository).getLocationPermission();
        verify(mockLocationPermissionRepository).updateLocationPermission(false);
        verifyNoMoreInteractions(mockLocationPermissionRepository);
    }

    @Test
    public void verify_denyLocationPermission_when_currentPermissionIsNull() {
        // GIVEN
        locationPermissionMutableLiveData.setValue(null);

        // WHEN
        setLocationPermissionUseCase.setPermission(false);

        // THEN
        verify(mockLocationPermissionRepository).getLocationPermission();
        verify(mockLocationPermissionRepository).updateLocationPermission(false);
        verifyNoMoreInteractions(mockLocationPermissionRepository);
    }

    @Test
    public void verify_doNotDenyLocationPermission_when_currentPermissionIsAlreadyDenied() {
        // GIVEN
        locationPermissionMutableLiveData.setValue(false);

        // WHEN
        setLocationPermissionUseCase.setPermission(false);

        // THEN
        verify(mockLocationPermissionRepository).getLocationPermission();
        verify(mockLocationPermissionRepository, never()).updateLocationPermission(false);
        verifyNoMoreInteractions(mockLocationPermissionRepository);
    }
}