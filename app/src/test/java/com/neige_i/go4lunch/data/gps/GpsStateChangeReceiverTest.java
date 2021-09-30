package com.neige_i.go4lunch.data.gps;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class GpsStateChangeReceiverTest {
    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GpsStateChangeReceiver gpsStateChangeReceiver;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationManager locationManagerMock = mock(LocationManager.class);

    private final Intent intentMock = mock(Intent.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() throws Exception {
        // Setup mocks
        doReturn(LocationManager.PROVIDERS_CHANGED_ACTION).when(intentMock).getAction();
        doReturn(true).when(locationManagerMock).isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Init Receiver
        gpsStateChangeReceiver = new GpsStateChangeReceiver(locationManagerMock);
    }

    // -------------------------------------- RECEIVER TESTS ---------------------------------------

    @Test
    public void setFirstGpsState_when_receiverIsInitialized() throws InterruptedException {
        // WHEN
        final boolean isGpsEnabled = getOrAwaitValue(gpsStateChangeReceiver.getGpsState());

        // THEN
        assertTrue(isGpsEnabled);
    }

    @Test
    public void setGpsStateToTrue_when_receiverIsTriggeredAndGpsIsEnabled() throws InterruptedException {
        // WHEN
        gpsStateChangeReceiver.onReceive(mock(Context.class), intentMock);
        final boolean isGpsEnabled = getOrAwaitValue(gpsStateChangeReceiver.getGpsState());

        // THEN
        assertTrue(isGpsEnabled);
    }

    @Test
    public void setGpsStateToFalse_when_receiverIsTriggeredAndGpsIsDisabled() throws InterruptedException {
        // GIVEN
        doReturn(false).when(locationManagerMock).isProviderEnabled(LocationManager.GPS_PROVIDER);

        // WHEN
        gpsStateChangeReceiver.onReceive(mock(Context.class), intentMock);
        final boolean isGpsEnabled = getOrAwaitValue(gpsStateChangeReceiver.getGpsState());

        // THEN
        assertFalse(isGpsEnabled);
    }
}