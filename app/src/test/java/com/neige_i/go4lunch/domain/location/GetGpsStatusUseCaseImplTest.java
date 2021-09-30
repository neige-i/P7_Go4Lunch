package com.neige_i.go4lunch.domain.location;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.gps.GpsStateChangeReceiver;
import com.neige_i.go4lunch.domain.gps.GetGpsStatusUseCase;
import com.neige_i.go4lunch.domain.gps.GetGpsStatusUseCaseImpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class GetGpsStatusUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetGpsStatusUseCase getGpsStatusUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GpsStateChangeReceiver gpsStateChangeReceiverMock = mock(GpsStateChangeReceiver.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Boolean> gpsEnableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(gpsEnableMutableLiveData).when(gpsStateChangeReceiverMock).getGpsState();

        // Init UseCase
        getGpsStatusUseCase = new GetGpsStatusUseCaseImpl(gpsStateChangeReceiverMock);
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