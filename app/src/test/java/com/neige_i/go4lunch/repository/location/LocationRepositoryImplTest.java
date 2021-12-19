package com.neige_i.go4lunch.repository.location;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class LocationRepositoryImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FusedLocationProviderClient fusedLocationProviderClientMock = mock(FusedLocationProviderClient.class);
    private final SettingsClient settingsClientMock = mock(SettingsClient.class);
    private final Looper looperMock = mock(Looper.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private LocationRepository locationRepository;

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    @SuppressWarnings("unchecked")
    private final Task<LocationSettingsResponse> locationSettingsResponseTaskMock = mock(Task.class);
    private final ResolvableApiException resolvableApiExceptionMock = mock(ResolvableApiException.class);

    // ------------------------------------- ARGUMENT CAPTORS --------------------------------------

    private final ArgumentCaptor<OnFailureListener> onFailureListenerCaptor = ArgumentCaptor.forClass(OnFailureListener.class);

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() throws Exception {
        // Setup mocks
        doReturn(locationSettingsResponseTaskMock).when(settingsClientMock).checkLocationSettings(any());

        // Init repository
        locationRepository = new LocationRepositoryImpl(
            fusedLocationProviderClientMock,
            settingsClientMock,
            looperMock
        );
    }

    // ---------------------------------- LOCATION UPDATES TESTS -----------------------------------

    @Test
    public void requestLocationUpdatesOnce_when_startUpdatesMultipleTimes() {
        // GIVEN
        locationRepository.startLocationUpdates(); // Start updates once

        // WHEN
        locationRepository.startLocationUpdates(); // Start updates twice

        // THEN
        verify(fusedLocationProviderClientMock).requestLocationUpdates(any(), any(), any());
    }

    @Test
    public void removeLocationUpdates_when_stopUpdates_with_alreadyRequestedUpdates() {
        // GIVEN
        locationRepository.startLocationUpdates(); // Start updates

        // WHEN
        locationRepository.removeLocationUpdates(); // Stop updates

        // THEN
        verify(fusedLocationProviderClientMock).removeLocationUpdates(any(LocationCallback.class));
    }

    @Test
    public void doNothing_when_stopUpdates_with_neverRequestedUpdates() {
        // GIVEN (do not start updates)

        // WHEN
        locationRepository.removeLocationUpdates(); // Stop updates

        // THEN
        verify(fusedLocationProviderClientMock, never()).removeLocationUpdates(any(LocationCallback.class));
    }

    @Test
    public void returnFalse_when_getLocationUpdatesStatus_with_nonNullLocationCallback() {
        // GIVEN
        locationRepository.startLocationUpdates(); // Start location updates to set LocationCallback

        // WHEN
        final boolean locationUpdatesNeverStarted = locationRepository.areLocationUpdatesNeverStarted();

        // THEN
        assertFalse(locationUpdatesNeverStarted);
    }

    @Test
    public void returnTrue_when_getLocationUpdatesStatus_with_nullLocationCallback() {
        // GIVEN (do not start updates)

        // WHEN
        final boolean locationUpdatesNeverStarted = locationRepository.areLocationUpdatesNeverStarted();

        // THEN
        assertTrue(locationUpdatesNeverStarted);
    }

    // ------------------------------------- GPS DIALOG TESTS --------------------------------------

    @Test
    public void updateGpsDialog_when_requestDialog_with_correctExceptionStatusCode() {
        // GIVEN
        doReturn(LocationSettingsStatusCodes.RESOLUTION_REQUIRED).when(resolvableApiExceptionMock).getStatusCode();

        // WHEN
        locationRepository.requestGpsDialog();

        // Capture OnFailureListener
        verify(locationSettingsResponseTaskMock).addOnFailureListener(onFailureListenerCaptor.capture());
        onFailureListenerCaptor.getValue().onFailure(resolvableApiExceptionMock);

        final ResolvableApiException gpsDialog = getValueForTesting(locationRepository.getGpsDialog());

        // THEN
        verify(settingsClientMock).checkLocationSettings(any());
        assertEquals(resolvableApiExceptionMock, gpsDialog);
    }

    @Test
    public void doNotUpdateGpsDialog_when_requestDialog_with_wrongExceptionStatusCode() {
        // GIVEN
        doReturn(-1).when(resolvableApiExceptionMock).getStatusCode();

        // WHEN
        locationRepository.requestGpsDialog();

        // Capture OnFailureListener
        verify(locationSettingsResponseTaskMock).addOnFailureListener(onFailureListenerCaptor.capture());
        onFailureListenerCaptor.getValue().onFailure(resolvableApiExceptionMock);

        final int gpsDialogTrigger = getLiveDataTriggerCount(locationRepository.getGpsDialog());

        // THEN
        verify(settingsClientMock).checkLocationSettings(any());
        assertEquals(0, gpsDialogTrigger); // Never updated
    }
}