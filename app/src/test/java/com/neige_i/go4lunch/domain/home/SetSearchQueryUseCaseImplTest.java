package com.neige_i.go4lunch.domain.home;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.data.google_places.AutocompleteRepository;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;

import org.junit.Rule;
import org.junit.Test;

public class SetSearchQueryUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final AutocompleteRepository autocompleteRepositoryMock = mock(AutocompleteRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final SetSearchQueryUseCase setSearchQueryUseCase = new SetSearchQueryUseCaseImpl(autocompleteRepositoryMock);

    // --------------------------------- SET CURRENT SEARCH TESTS ----------------------------------

    @Test
    public void updateCurrentSearch_when_launchSearch() {
        // GIVEN
        final AutocompleteRestaurant autocompleteRestaurant = new AutocompleteRestaurant(
            "PLACE_ID",
            "RESTAURANT_NAME"
        );

        // WHEN
        setSearchQueryUseCase.launch(autocompleteRestaurant);

        // THEN
        verify(autocompleteRepositoryMock).setCurrentSearch(autocompleteRestaurant);
        verifyNoMoreInteractions(autocompleteRepositoryMock);
    }

    @Test
    public void resetCurrentSearch_when_closeSearch() {
        // WHEN
        setSearchQueryUseCase.close();

        // THEN
        verify(autocompleteRepositoryMock).setCurrentSearch(null);
        verifyNoMoreInteractions(autocompleteRepositoryMock);
    }
}