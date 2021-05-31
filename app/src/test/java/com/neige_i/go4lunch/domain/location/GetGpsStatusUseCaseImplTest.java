package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GetGpsStatusUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetGpsStatusUseCase getGpsStatusUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> gpsEnableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(gpsEnableMutableLiveData).when(locationRepositoryMock).isGpsEnabled();

        // Init UseCase
        getGpsStatusUseCase = new GetGpsStatusUseCaseImpl(locationRepositoryMock);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnTrue_when_gpsIsEnabled() throws InterruptedException {
        // GIVEN
        gpsEnableMutableLiveData.setValue(true);

        // WHEN
        final boolean isLocationAvailable = getOrAwaitValue(getGpsStatusUseCase.isEnabled());

        // THEN
        assertTrue(isLocationAvailable);
    }

    @Test
    public void returnFalse_when_gpsIsDisabled() throws InterruptedException {
        // GIVEN
        gpsEnableMutableLiveData.setValue(false);

        // WHEN
        final boolean isLocationAvailable = getOrAwaitValue(getGpsStatusUseCase.isEnabled());

        // THEN
        assertFalse(isLocationAvailable);
    }
}