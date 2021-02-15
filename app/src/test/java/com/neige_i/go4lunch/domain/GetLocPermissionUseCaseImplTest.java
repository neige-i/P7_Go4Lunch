package com.neige_i.go4lunch.domain;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GetLocPermissionUseCaseImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Values returned from mocked objects
    private final MutableLiveData<Boolean> permissionGranted = new MutableLiveData<>();

    // Mock objects
    private final LocationRepository locationRepository = mock(LocationRepository.class);

    // UseCase under test
    private GetLocPermissionUseCase getLocPermissionUseCase;

    @Before
    public void setUp() {
        doReturn(permissionGranted).when(locationRepository).getLocationPermission();

        getLocPermissionUseCase = new GetLocPermissionUseCaseImpl(locationRepository);
    }

    @Test
    public void nominalCase_permissionGranted() throws InterruptedException {
        // Given
        permissionGranted.setValue(true);

        // Then
        assertTrue(getOrAwaitValue(getLocPermissionUseCase.isPermissionGranted()));

        verify(locationRepository).startLocationUpdates();
    }

    @Test
    public void altCase_permissionDenied() throws InterruptedException {
        // Given
        permissionGranted.setValue(false);

        // Then
        assertFalse(getOrAwaitValue(getLocPermissionUseCase.isPermissionGranted()));

        verify(locationRepository).removeLocationUpdates();
    }

    @Test
    public void edgeCase_noPermissionRetrieved() {
        // Given: omit setValue

        // When
        final Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> getOrAwaitValue(getLocPermissionUseCase.isPermissionGranted())
        );

        // Then
        assertEquals("LiveData value was never set.", thrownException.getMessage());
    }
}