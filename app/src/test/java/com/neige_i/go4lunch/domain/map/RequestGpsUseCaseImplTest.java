package com.neige_i.go4lunch.domain.map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Rule;
import org.junit.Test;

public class RequestGpsUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final RequestGpsUseCase requestGpsUseCase = new RequestGpsUseCaseImpl(locationRepositoryMock);

    // ------------------------------------- REQUEST GPS TESTS -------------------------------------

    @Test
    public void requestGps_when_request() {
        // WHEN
        requestGpsUseCase.request();

        // THEN
        verify(locationRepositoryMock).requestGpsDialog();
        verifyNoMoreInteractions(locationRepositoryMock);
    }
}