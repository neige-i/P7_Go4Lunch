package com.neige_i.go4lunch.domain.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.data.preferences.PreferencesRepository;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetNotificationInfoUseCaseImplTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final PreferencesRepository preferencesRepositoryMock = mock(PreferencesRepository.class);
    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    private final WorkmatesDelegate workmatesDelegateMock = mock(WorkmatesDelegate.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetNotificationInfoUseCase getNotificationInfoUseCase;

    // ------------------------------------------- CONST -------------------------------------------

    private static final String USER_ID = "userID";
    private static final String NAME = "name";
    private static final String RESTAURANT_NAME = "restaurantName";
    private static final String RESTAURANT_ADDRESS = "restaurantAddress";
    private static final String PLACE_ID = "placeID";
    private static final String DATE = "18/12/2021";

    @Before
    public void setUp() {
        doReturn(true).when(preferencesRepositoryMock).getMiddayNotificationEnabled();
        final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);
        doReturn(firebaseUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn(USER_ID).when(firebaseUserMock).getUid();
        doReturn(getCurrentUser()).when(firestoreRepositoryMock).getUserByIdSync(USER_ID);
        doReturn(true).when(workmatesDelegateMock).isToday(DATE);
        doReturn(getDefaultWorkmateList()).when(firestoreRepositoryMock).getWorkmatesEatingAtSync(PLACE_ID);

        getNotificationInfoUseCase = new GetNotificationInfoUseCaseImpl(
            preferencesRepositoryMock,
            firestoreRepositoryMock,
            firebaseAuthMock,
            workmatesDelegateMock
        );
    }

    @Test
    public void returnNull_when_getInfo_withDisabledMiddayNotification() {
        // GIVEN
        doReturn(false).when(preferencesRepositoryMock).getMiddayNotificationEnabled();

        // WHEN
        final NotificationInfo notificationInfo = getNotificationInfoUseCase.get();

        // THEN
        assertNull(notificationInfo);
    }

    @Test
    public void returnNull_when_getInfo_withNullFirebaseUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        final NotificationInfo notificationInfo = getNotificationInfoUseCase.get();

        // THEN
        assertNull(notificationInfo);
    }

    @Test
    public void returnNull_when_getInfo_withNullFirestoreUser() {
        // GIVEN
        doReturn(null).when(firestoreRepositoryMock).getUserByIdSync(USER_ID);

        // WHEN
        final NotificationInfo notificationInfo = getNotificationInfoUseCase.get();

        // THEN
        assertNull(notificationInfo);
    }

    @Test
    public void returnNull_when_getInfo_withNoSelectedRestaurant() {
        // GIVEN
        doReturn(new User(
            "@me",
            "myName",
            "myPhoto",
            null, // No selected restaurant
            null
        )).when(firestoreRepositoryMock).getUserByIdSync(USER_ID);

        // WHEN
        final NotificationInfo notificationInfo = getNotificationInfoUseCase.get();

        // THEN
        assertNull(notificationInfo);
    }

    @Test
    public void returnNull_when_getInfo_withSelectedRestaurantNotToday() {
        // GIVEN
        doReturn(false).when(workmatesDelegateMock).isToday(DATE);

        // WHEN
        final NotificationInfo notificationInfo = getNotificationInfoUseCase.get();

        // THEN
        assertNull(notificationInfo);
    }

    @Test
    public void returnNotificationInfoWithoutWorkmates_when_getInfo_withNoInterestedWorkmates() {
        // GIVEN
        doReturn(Collections.singletonList(getCurrentUser()))
            .when(firestoreRepositoryMock).getWorkmatesEatingAtSync(PLACE_ID);

        // WHEN
        final NotificationInfo notificationInfo = getNotificationInfoUseCase.get();

        // THEN
        assertEquals(
            new NotificationInfo(
                PLACE_ID,
                RESTAURANT_NAME,
                RESTAURANT_ADDRESS,
                Collections.emptyList()
            ),
            notificationInfo
        );
    }

    @Test
    public void returnNotificationInfo_when_getInfo_withAllConditionsAreMet() {
        // WHEN
        final NotificationInfo notificationInfo = getNotificationInfoUseCase.get();

        // THEN
        assertEquals(
            new NotificationInfo(
                PLACE_ID,
                RESTAURANT_NAME,
                RESTAURANT_ADDRESS,
                Arrays.asList(
                    NAME + 0,
                    NAME + 1,
                    NAME + 2
                )
            ),
            notificationInfo
        );
    }

    // ------------------------------------------- UTIL --------------------------------------------

    @NonNull
    private User getCurrentUser() {
        return new User(
            "@me",
            "myName",
            "myPhoto",
            new User.SelectedRestaurant(PLACE_ID, DATE, RESTAURANT_NAME, RESTAURANT_ADDRESS),
            null
        );
    }

    @NonNull
    private List<User> getDefaultWorkmateList() {
        return Arrays.asList(
            getDefaultWorkmate(0),
            getDefaultWorkmate(1),
            getDefaultWorkmate(2),
            getCurrentUser()
        );
    }

    @NonNull
    private User getDefaultWorkmate(int index) {
        return new User(
            "email",
            NAME + index,
            "photo",
            null,
            null
        );
    }
}