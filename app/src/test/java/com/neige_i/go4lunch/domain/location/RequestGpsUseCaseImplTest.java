package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class RequestGpsUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private RequestGpsUseCase requestGpsUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Init UseCase
        requestGpsUseCase = new RequestGpsUseCaseImpl(locationRepositoryMock);
    }

    // ------------------------------------------ VERIFY -------------------------------------------

    @Test
    public void verify_requestGps_when_gpsNeedToBeEnabled() {
        // WHEN
        requestGpsUseCase.request();

        // THEN
        verify(locationRepositoryMock).requestGps();
        verifyNoMoreInteractions(locationRepositoryMock);
    }
}