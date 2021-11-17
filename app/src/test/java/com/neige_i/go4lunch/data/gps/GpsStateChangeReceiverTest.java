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

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final Context contextMock = mock(Context.class);

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final LocationManager locationManagerMock = mock(LocationManager.class);
    private final Intent intentMock = mock(Intent.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GpsStateChangeReceiver gpsStateChangeReceiver;

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(locationManagerMock).when(contextMock).getSystemService(Context.LOCATION_SERVICE);
        doReturn(LocationManager.PROVIDERS_CHANGED_ACTION).when(intentMock).getAction();
        doReturn(LocationManager.GPS_PROVIDER).when(intentMock).getStringExtra(LocationManager.EXTRA_PROVIDER_NAME);
        doReturn(true).when(locationManagerMock).isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Init Receiver
        gpsStateChangeReceiver = new GpsStateChangeReceiver(contextMock);
    }

    // -------------------------------------- RECEIVER TESTS ---------------------------------------

    @Test
    public void returnTrue_when_receiverIsInitializedAndGpsIsEnabled() throws InterruptedException {
        // GIVEN
        // GPS is enabled in @Before

        // WHEN
        final boolean isGpsEnabled = getOrAwaitValue(gpsStateChangeReceiver.getGpsState());

        // THEN
        assertTrue(isGpsEnabled);
    }

    @Test
    public void returnFalse_when_receiverIsInitializedAndGpsIsDisabled() throws InterruptedException {
        // GIVEN
        doReturn(false).when(locationManagerMock).isProviderEnabled(LocationManager.GPS_PROVIDER);

        // WHEN
        final boolean isGpsEnabled = getOrAwaitValue(gpsStateChangeReceiver.getGpsState());

        // THEN
        assertTrue(isGpsEnabled);
    }

    @Test
    public void returnTrue_when_receiverIsTriggeredAndGpsIsEnabled() throws InterruptedException {
        // GIVEN
        // GPS is enabled in @Before

        // WHEN
        gpsStateChangeReceiver.onReceive(mock(Context.class), intentMock);
        final boolean isGpsEnabled = getOrAwaitValue(gpsStateChangeReceiver.getGpsState());

        // THEN
        assertTrue(isGpsEnabled);
    }

    @Test
    public void returnFalse_when_receiverIsTriggeredAndGpsIsDisabled() throws InterruptedException {
        // GIVEN
        doReturn(false).when(locationManagerMock).isProviderEnabled(LocationManager.GPS_PROVIDER);

        // WHEN
        gpsStateChangeReceiver.onReceive(mock(Context.class), intentMock);
        final boolean isGpsEnabled = getOrAwaitValue(gpsStateChangeReceiver.getGpsState());

        // THEN
        assertFalse(isGpsEnabled);
    }
}