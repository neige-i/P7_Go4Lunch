package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class StopLocationUpdatesUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private StopLocationUpdatesUseCase stopLocationUpdatesUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository mockLocationRepository = mock(LocationRepository.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        stopLocationUpdatesUseCase = new StopLocationUpdatesUseCaseImpl(mockLocationRepository);
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_removeLocationUpdates() {
        // WHEN
        stopLocationUpdatesUseCase.stopUpdates();

        // THEN
        verify(mockLocationRepository).removeLocationUpdates();
        verifyNoMoreInteractions(mockLocationRepository);
    }
}