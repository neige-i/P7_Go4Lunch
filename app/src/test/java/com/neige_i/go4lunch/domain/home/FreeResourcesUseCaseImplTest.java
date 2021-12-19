package com.neige_i.go4lunch.domain.home;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.repository.firestore.FirestoreRepository;
import com.neige_i.go4lunch.repository.location.LocationRepository;

import org.junit.Rule;
import org.junit.Test;

public class FreeResourcesUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);
    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final FreeResourcesUseCase freeResourcesUseCase = new FreeResourcesUseCaseImpl(
        locationRepositoryMock,
        firestoreRepositoryMock
    );

    // ----------------------------------- FREE RESOURCES TESTS ------------------------------------

    @Test
    public void removeListeners_when_execute() {
        // WHEN
        freeResourcesUseCase.execute();

        // THEN
        verify(locationRepositoryMock).removeLocationUpdates();
        verify(firestoreRepositoryMock).removeListenerRegistrations();
        verifyNoMoreInteractions(locationRepositoryMock, firestoreRepositoryMock);
    }
}