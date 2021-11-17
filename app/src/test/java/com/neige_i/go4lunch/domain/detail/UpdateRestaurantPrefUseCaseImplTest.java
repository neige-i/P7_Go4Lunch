package com.neige_i.go4lunch.domain.detail;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UpdateRestaurantPrefUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private UpdateRestaurantPrefUseCase updateRestaurantPrefUseCase;

    // ------------------------------------------- CONST -------------------------------------------

    private static final String USER_ID = "USER_ID";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);
        doReturn(firebaseUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn(USER_ID).when(firebaseUserMock).getUid();

        // Init UseCase
        updateRestaurantPrefUseCase = new UpdateRestaurantPrefUseCaseImpl(
            firestoreRepositoryMock,
            firebaseAuthMock
        );
    }

    // ---------------------------------- VERIFY REPOSITORY CALLs ----------------------------------

    @Test
    public void addFavorite_when_restaurantIsLiked_with_nonNullCurrentUser() {
        // WHEN
        updateRestaurantPrefUseCase.like("PLACE_ID");

        // THEN
        verify(firestoreRepositoryMock).addToFavoriteRestaurant(USER_ID, "PLACE_ID");
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void removeFavorite_when_restaurantIsUnliked_with_nonNullCurrentUser() {
        // WHEN
        updateRestaurantPrefUseCase.unlike("PLACE_ID");

        // THEN
        verify(firestoreRepositoryMock).removeFromFavoriteRestaurant(USER_ID, "PLACE_ID");
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void setSelected_when_restaurantIsSelected_with_nonNullCurrentUser() {
        // WHEN
        updateRestaurantPrefUseCase.select("PLACE_ID", "NAME");

        // THEN
        verify(firestoreRepositoryMock).setSelectedRestaurant(USER_ID, "PLACE_ID", "NAME");
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void clearSelected_when_restaurantIsUnselected_with_nonNullCurrentUser() {
        // WHEN
        updateRestaurantPrefUseCase.unselect();

        // THEN
        verify(firestoreRepositoryMock).clearSelectedRestaurant(USER_ID);
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_restaurantIsLiked_with_nullCurrentUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        updateRestaurantPrefUseCase.like("PLACE_ID");

        // THEN
        verify(firestoreRepositoryMock, never()).addToFavoriteRestaurant(USER_ID, "PLACE_ID");
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_restaurantIsUnliked_with_nullCurrentUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        updateRestaurantPrefUseCase.unlike("PLACE_ID");

        // THEN
        verify(firestoreRepositoryMock, never()).removeFromFavoriteRestaurant(USER_ID, "PLACE_ID");
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_restaurantIsSelected_with_nullCurrentUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        updateRestaurantPrefUseCase.select("PLACE_ID", "NAME");

        // THEN
        verify(firestoreRepositoryMock, never()).setSelectedRestaurant(USER_ID, "PLACE_ID", "NAME");
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_restaurantIsUnselected_with_nullCurrentUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        updateRestaurantPrefUseCase.unselect();

        // THEN
        verify(firestoreRepositoryMock, never()).clearSelectedRestaurant(USER_ID);
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }
}