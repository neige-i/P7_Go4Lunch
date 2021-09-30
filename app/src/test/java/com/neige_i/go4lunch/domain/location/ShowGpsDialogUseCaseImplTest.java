package com.neige_i.go4lunch.domain.location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.domain.gps.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.gps.ShowGpsDialogUseCaseImpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ShowGpsDialogUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private ShowGpsDialogUseCase showGpsDialogUseCase;

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<ResolvableApiException> resolvableMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(resolvableMutableLiveData).when(locationRepositoryMock).getGpsDialog();

        // Init UseCase
        showGpsDialogUseCase = new ShowGpsDialogUseCaseImpl(locationRepositoryMock);
    }

    // ------------------------------------------- TESTS -------------------------------------------

    @Test
    public void returnGpsDialog_when_dialogIsQueried() throws InterruptedException {
        // GIVEN
        final ResolvableApiException expectedGpsDialog = mock(ResolvableApiException.class);
        resolvableMutableLiveData.setValue(expectedGpsDialog);

        // WHEN
        final ResolvableApiException actualGpsDialog = getOrAwaitValue(showGpsDialogUseCase.getDialog());

        // THEN
        assertEquals(expectedGpsDialog, actualGpsDialog);
    }
}