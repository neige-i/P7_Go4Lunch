package com.neige_i.go4lunch.domain.home;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Rule;
import org.junit.Test;

public class SetLocationUpdatesUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final SetLocationUpdatesUseCase setLocationUpdatesUseCase = new SetLocationUpdatesUseCaseImpl(
        locationRepositoryMock
    );

    // ---------------------------------- LOCATION UPDATES TESTS -----------------------------------

    @Test
    public void startLocationUpdatesAndRequestGps_when_updatesAreEnabledTheFirstTime() {
        // GIVEN
        doReturn(true).when(locationRepositoryMock).areLocationUpdatesNeverStarted();

        // WHEN
        setLocationUpdatesUseCase.set(true);

        // THEN
        verify(locationRepositoryMock).areLocationUpdatesNeverStarted();
        verify(locationRepositoryMock).requestGpsDialog();
        verify(locationRepositoryMock).startLocationUpdates();
        verifyNoMoreInteractions(locationRepositoryMock);
    }

    @Test
    public void startLocationUpdatesOnly_when_updatesAreEnabledAgain() {
        // GIVEN
        doReturn(false).when(locationRepositoryMock).areLocationUpdatesNeverStarted();

        // WHEN
        setLocationUpdatesUseCase.set(true);

        // THEN
        verify(locationRepositoryMock).areLocationUpdatesNeverStarted();
        verify(locationRepositoryMock, never()).requestGpsDialog();
        verify(locationRepositoryMock).startLocationUpdates();
        verifyNoMoreInteractions(locationRepositoryMock);
    }

    @Test
    public void removeLocationUpdates_when_updatesAreDisabled() {
        // WHEN
        setLocationUpdatesUseCase.set(false);

        // THEN
        verify(locationRepositoryMock).removeLocationUpdates();
        verifyNoMoreInteractions(locationRepositoryMock);
    }
}