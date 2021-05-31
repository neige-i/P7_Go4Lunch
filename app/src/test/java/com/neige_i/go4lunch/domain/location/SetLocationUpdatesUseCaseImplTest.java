package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class SetLocationUpdatesUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private SetLocationUpdatesUseCase setLocationUpdatesUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        setLocationUpdatesUseCase = new SetLocationUpdatesUseCaseImpl(locationRepositoryMock);
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_startLocationUpdates_when_updatesAreEnabled() {
        // WHEN
        setLocationUpdatesUseCase.set(true);

        // THEN
        verify(locationRepositoryMock).startLocationUpdates();
        verifyNoMoreInteractions(locationRepositoryMock);
    }

    @Test
    public void verify_removeLocationUpdates_when_updatesAreDisabled() {
        // WHEN
        setLocationUpdatesUseCase.set(false);

        // THEN
        verify(locationRepositoryMock).removeLocationUpdates();
        verifyNoMoreInteractions(locationRepositoryMock);
    }
}