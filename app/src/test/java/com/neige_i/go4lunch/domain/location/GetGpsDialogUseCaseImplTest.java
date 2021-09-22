package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.domain.gps.GetGpsDialogUseCase;
import com.neige_i.go4lunch.domain.gps.GetGpsDialogUseCaseImpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GetGpsDialogUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetGpsDialogUseCase getGpsDialogUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(resolvableMutableLiveData).when(locationRepositoryMock).getGpsDialogPrompt();

        // Init UseCase
        getGpsDialogUseCase = new GetGpsDialogUseCaseImpl(locationRepositoryMock);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnResolvableApiExceptionInstance_when_IsQueried() throws InterruptedException {
        // GIVEN
        final ResolvableApiException expectedResolvable = mock(ResolvableApiException.class);
        resolvableMutableLiveData.setValue(expectedResolvable);

        // WHEN
        final ResolvableApiException actualResolvable = getOrAwaitValue(getGpsDialogUseCase.showDialog());

        // THEN
        assertEquals(expectedResolvable, actualResolvable);
    }
}