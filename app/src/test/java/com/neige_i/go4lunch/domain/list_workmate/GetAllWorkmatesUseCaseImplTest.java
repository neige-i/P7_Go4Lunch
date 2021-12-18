package com.neige_i.go4lunch.domain.list_workmate;

import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GetAllWorkmatesUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    private final WorkmatesDelegate workmatesDelegateMock = mock(WorkmatesDelegate.class);

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetAllWorkmatesUseCase getAllWorkmatesUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(userListMutableLiveData).when(firestoreRepositoryMock).getAllUsers();
        doCallRealMethod().when(workmatesDelegateMock).moveToFirstPosition(anyList(), any());

        // Init UseCase
        getAllWorkmatesUseCase = new GetAllWorkmatesUseCaseImpl(
            firestoreRepositoryMock,
            firebaseAuthMock,
            workmatesDelegateMock
        );

        // Default behaviour
        userListMutableLiveData.setValue(Arrays.asList(
            new User("EMAIL2", "NAME2", "PHOTO2", null, null),
            new User("EMAIL1", "NAME1", "PHOTO1", new User.SelectedRestaurant("PLACE_ID1", "15/11/2021", "RESTAURANT1", "ADDRESS"), null)
        ));
    }

    // ------------------------------------- DEPENDENCY TESTS --------------------------------------

    @Test
    public void returnWorkmateList_when_getValue_with_nullCurrentUserAndToday() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();
        doReturn(true).when(workmatesDelegateMock).isToday("15/11/2021");

        // WHEN
        final List<Workmate> workmateList = getValueForTesting(getAllWorkmatesUseCase.get());

        // THEN
        assertEquals(
            // None of the users is the current one as FirebaseUser is null
            Arrays.asList(
                new Workmate.WithoutRestaurant("EMAIL2", "NAME2", "PHOTO2", false),
                new Workmate.WithRestaurant("EMAIL1", "NAME1", "PHOTO1", false, "PLACE_ID1", "RESTAURANT1")
            ),
            workmateList
        );
    }

    @Test
    public void returnWorkmateList_when_getValue_with_nonNullCurrentUserAndNotToday() {
        // GIVEN
        doReturn(firebaseUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn("EMAIL1").when(firebaseUserMock).getEmail();
        doReturn(false).when(workmatesDelegateMock).isToday("15/11/2021");

        // WHEN
        final List<Workmate> workmateList = getValueForTesting(getAllWorkmatesUseCase.get());

        // THEN
        assertEquals(
            // None of the users has selected a restaurant for the day as isToday() returns false
            Arrays.asList(
                new Workmate.WithoutRestaurant("EMAIL1", "NAME1", "PHOTO1", true), // 1st position
                new Workmate.WithoutRestaurant("EMAIL2", "NAME2", "PHOTO2", false)
            ),
            workmateList
        );
    }
}