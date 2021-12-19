package com.neige_i.go4lunch.domain.home;

import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.repository.firestore.FirestoreRepository;
import com.neige_i.go4lunch.repository.firestore.User;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class GetDrawerInfoUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    private final WorkmatesDelegate workmatesDelegateMock = mock(WorkmatesDelegate.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetDrawerInfoUseCase getDrawerInfoUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);

    // ------------------------------------------- CONST -------------------------------------------

    private static final String USER_ID = "USER_ID";
    private static final String TODAY = "10/12/2021";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(userMutableLiveData).when(firestoreRepositoryMock).getUser(USER_ID);
        doReturn(firebaseUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn(USER_ID).when(firebaseUserMock).getUid();
        doReturn(true).when(workmatesDelegateMock).isToday(TODAY);

        // Init UseCase
        getDrawerInfoUseCase = new GetDrawerInfoUseCaseImpl(
            firestoreRepositoryMock,
            firebaseAuthMock,
            workmatesDelegateMock
        );

        // Default behaviour
        userMutableLiveData.setValue(new User(
            USER_ID,
            "email",
            "name",
            "photo",
            new User.SelectedRestaurant("placeId", TODAY, "restaurantName", "ADDRESS"),
            null
        ));
    }

    // ------------------------------------ GET USER INFO TESTS ------------------------------------

    @Test
    public void returnDrawerInfo_when_getUserInfo_with_todaySelectedRestaurant() {
        // GIVEN (restaurant is selected today in @Before)

        // WHEN
        final DrawerInfo drawerInfo = getValueForTesting(getDrawerInfoUseCase.get());

        // THEN
        assertEquals(
            new DrawerInfo(
                "photo",
                "name",
                "email",
                "placeId"
            ),
            drawerInfo
        );
    }

    @Test
    public void returnDrawerInfoWithoutRestaurant_when_getUserInfo_with_notTodaySelectedRestaurant() {
        // GIVEN
        doReturn(false).when(workmatesDelegateMock).isToday(TODAY);

        // WHEN
        final DrawerInfo drawerInfo = getValueForTesting(getDrawerInfoUseCase.get());

        // THEN
        assertEquals(
            new DrawerInfo(
                "photo",
                "name",
                "email",
                null
            ),
            drawerInfo
        );
    }

    @Test
    public void returnDrawerInfoWithoutRestaurant_when_getUserInfo_with_noSelectedRestaurant() {
        // GIVEN
        userMutableLiveData.setValue(new User(
            USER_ID,
            "email",
            "name",
            "photo",
            null,
            null
        ));

        // WHEN
        final DrawerInfo drawerInfo = getValueForTesting(getDrawerInfoUseCase.get());

        // THEN
        assertEquals(
            new DrawerInfo(
                "photo",
                "name",
                "email",
                null
            ),
            drawerInfo
        );
    }

    @Test
    public void returnNullDrawerInfo_when_getUserInfo_with_nullFirebaseUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        final DrawerInfo drawerInfo = getValueForTesting(getDrawerInfoUseCase.get());

        // THEN
        assertNull(
            drawerInfo
        );
    }
}