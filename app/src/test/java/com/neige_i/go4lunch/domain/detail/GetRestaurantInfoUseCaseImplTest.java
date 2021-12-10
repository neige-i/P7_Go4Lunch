package com.neige_i.go4lunch.domain.detail;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
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
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetRestaurantInfoUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final DetailsRepository detailsRepositoryMock = mock(DetailsRepository.class);
    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    private final WorkmatesDelegate workmatesDelegateMock = mock(WorkmatesDelegate.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetRestaurantInfoUseCase getRestaurantInfoUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<RestaurantDetails> restaurantDetailsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> interestedWorkmatesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> currentUserMutableLiveData = new MutableLiveData<>();

    // ------------------------------------------- CONST -------------------------------------------

    private static final String PLACE_ID = "PLACE_ID";
    private static final String TODAY_DATE = "15/11/2021";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        final FirebaseUser firebaseUser = mock(FirebaseUser.class);
        final String currentUserId = "USER_ID";
        doReturn(restaurantDetailsMutableLiveData).when(detailsRepositoryMock).getData(PLACE_ID);
        doReturn(interestedWorkmatesMutableLiveData).when(firestoreRepositoryMock).getWorkmatesEatingAt(PLACE_ID);
        doReturn(firebaseUser).when(firebaseAuthMock).getCurrentUser();
        doReturn(currentUserId).when(firebaseUser).getUid();
        doReturn(currentUserMutableLiveData).when(firestoreRepositoryMock).getUser(currentUserId);
        doCallRealMethod().when(workmatesDelegateMock).moveToFirstPosition(anyList(), any());
        doReturn(true).when(workmatesDelegateMock).isToday(TODAY_DATE);

        // Init UseCase
        getRestaurantInfoUseCase = new GetRestaurantInfoUseCaseImpl(
            detailsRepositoryMock,
            firestoreRepositoryMock,
            firebaseAuthMock,
            workmatesDelegateMock
        );

        // Default behaviour
        final User currentUser = new User(
            "MY_EMAIL",
            "MY_NAME",
            "MY_PHOTO",
            new User.SelectedRestaurant(PLACE_ID, TODAY_DATE, "RESTAURANT_NAME"),
            Collections.singletonList(PLACE_ID)
        );
        restaurantDetailsMutableLiveData.setValue(new RestaurantDetails(
            PLACE_ID,
            "RESTAURANT_NAME",
            "ADDRESS",
            0,
            0,
            0,
            "PHOTO",
            "+33",
            "https://",
            Arrays.asList(
                new RestaurantDetails.RestaurantHour(true, DayOfWeek.MONDAY, LocalTime.of(10, 30)),
                new RestaurantDetails.RestaurantHour(false, DayOfWeek.MONDAY, LocalTime.of(13, 0))
            )
        ));
        interestedWorkmatesMutableLiveData.setValue(Arrays.asList(
            new User(
                "WORKMATE_EMAIL",
                "WORKMATE_NAME",
                "WORKMATE_PHOTO",
                null,
                null
            ),
            currentUser // Current user last
        ));
        currentUserMutableLiveData.setValue(currentUser);
    }

    // ------------------------------------- DEPENDENCY TESTS --------------------------------------

    @Test
    public void returnRestaurantInfo_when_getValue_with_defaultBehaviour() {
        // WHEN
        final RestaurantInfo restaurantInfo = getValueForTesting(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(
            new RestaurantInfo(
                "RESTAURANT_NAME",
                "ADDRESS",
                "PHOTO",
                0,
                "+33",
                "https://",
                true,
                true,
                Arrays.asList(
                    new CleanWorkmate("MY_EMAIL", "MY_NAME", "MY_PHOTO", true), // Current user first
                    new CleanWorkmate("WORKMATE_EMAIL", "WORKMATE_NAME", "WORKMATE_PHOTO", false)
                )
            ),
            restaurantInfo
        );
    }

    @Test
    public void returnRestaurantInfo_when_getValue_with_noFavoriteRestaurant() {
        // GIVEN
        currentUserMutableLiveData.setValue(new User(
            "MY_EMAIL",
            "MY_NAME",
            "MY_PHOTO",
            new User.SelectedRestaurant(PLACE_ID, TODAY_DATE, "RESTAURANT_NAME"),
            null // No favorite restaurants
        ));

        // WHEN
        final RestaurantInfo restaurantInfo = getValueForTesting(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(
            new RestaurantInfo(
                "RESTAURANT_NAME",
                "ADDRESS",
                "PHOTO",
                0,
                "+33",
                "https://",
                false, // Not favorite
                true,
                Arrays.asList(
                    new CleanWorkmate("MY_EMAIL", "MY_NAME", "MY_PHOTO", true),
                    new CleanWorkmate("WORKMATE_EMAIL", "WORKMATE_NAME", "WORKMATE_PHOTO", false)
                )
            ),
            restaurantInfo
        );
    }

    @Test
    public void returnRestaurantInfo_when_getValue_with_differentFavoriteRestaurant() {
        // GIVEN
        currentUserMutableLiveData.setValue(new User(
            "MY_EMAIL",
            "MY_NAME",
            "MY_PHOTO",
            new User.SelectedRestaurant(PLACE_ID, TODAY_DATE, "RESTAURANT_NAME"),
            Arrays.asList("OTHER_PLACE_ID", "ANOTHER_PLACE_ID") // Different favorite restaurants
        ));

        // WHEN
        final RestaurantInfo restaurantInfo = getValueForTesting(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(
            new RestaurantInfo(
                "RESTAURANT_NAME",
                "ADDRESS",
                "PHOTO",
                0,
                "+33",
                "https://",
                false, // Not favorite
                true,
                Arrays.asList(
                    new CleanWorkmate("MY_EMAIL", "MY_NAME", "MY_PHOTO", true),
                    new CleanWorkmate("WORKMATE_EMAIL", "WORKMATE_NAME", "WORKMATE_PHOTO", false)
                )
            ),
            restaurantInfo
        );
    }

    @Test
    public void returnRestaurantInfo_when_getValue_with_noSelectedRestaurant() {
        // GIVEN
        currentUserMutableLiveData.setValue(new User(
            "MY_EMAIL",
            "MY_NAME",
            "MY_PHOTO",
            null, // No selected restaurant
            Collections.singletonList(PLACE_ID)
        ));

        // WHEN
        final RestaurantInfo restaurantInfo = getValueForTesting(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(
            new RestaurantInfo(
                "RESTAURANT_NAME",
                "ADDRESS",
                "PHOTO",
                0,
                "+33",
                "https://",
                true,
                false, // Not selected
                Arrays.asList(
                    new CleanWorkmate("MY_EMAIL", "MY_NAME", "MY_PHOTO", true),
                    new CleanWorkmate("WORKMATE_EMAIL", "WORKMATE_NAME", "WORKMATE_PHOTO", false)
                )
            ),
            restaurantInfo
        );
    }

    @Test
    public void returnRestaurantInfo_when_getValue_with_differentRestaurantSelected() {
        // GIVEN
        currentUserMutableLiveData.setValue(new User(
            "MY_EMAIL",
            "MY_NAME",
            "MY_PHOTO",
            new User.SelectedRestaurant("OTHER_PLACE_ID", TODAY_DATE, "OTHER_RESTAURANT_NAME"), // Other selected restaurant
            Collections.singletonList(PLACE_ID)
        ));

        // WHEN
        final RestaurantInfo restaurantInfo = getValueForTesting(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(
            new RestaurantInfo(
                "RESTAURANT_NAME",
                "ADDRESS",
                "PHOTO",
                0,
                "+33",
                "https://",
                true,
                false, // Not selected
                Arrays.asList(
                    new CleanWorkmate("MY_EMAIL", "MY_NAME", "MY_PHOTO", true),
                    new CleanWorkmate("WORKMATE_EMAIL", "WORKMATE_NAME", "WORKMATE_PHOTO", false)
                )
            ),
            restaurantInfo
        );
    }

    @Test
    public void returnRestaurantInfo_when_getValue_with_differentSelectedDate() {
        // GIVEN
        doReturn(false).when(workmatesDelegateMock).isToday(TODAY_DATE);

        // WHEN
        final RestaurantInfo restaurantInfo = getValueForTesting(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(
            new RestaurantInfo(
                "RESTAURANT_NAME",
                "ADDRESS",
                "PHOTO",
                0,
                "+33",
                "https://",
                true,
                false, // Not selected
                Arrays.asList(
                    new CleanWorkmate("MY_EMAIL", "MY_NAME", "MY_PHOTO", true),
                    new CleanWorkmate("WORKMATE_EMAIL", "WORKMATE_NAME", "WORKMATE_PHOTO", false)
                )
            ),
            restaurantInfo
        );
    }

    @Test
    public void returnRestaurantInfo_when_getValue_with_noInterestedWorkmate() {
        // GIVEN
        interestedWorkmatesMutableLiveData.setValue(null);

        // WHEN
        final RestaurantInfo restaurantInfo = getValueForTesting(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(
            new RestaurantInfo(
                "RESTAURANT_NAME",
                "ADDRESS",
                "PHOTO",
                0,
                "+33",
                "https://",
                true,
                true,
                Collections.emptyList() // No workmate
            ),
            restaurantInfo
        );
    }

    @Test
    public void doNothing_when_getValue_with_unavailableCurrentUser() {
        // GIVEN
        doReturn(null).when(firebaseAuthMock).getCurrentUser();

        // WHEN
        final int getLiveDataTrigger = getLiveDataTriggerCount(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(0, getLiveDataTrigger);
    }

    @Test
    public void doNothing_when_getValue_with_unavailableRestaurantData() {
        // GIVEN
        restaurantDetailsMutableLiveData.setValue(null);

        // WHEN
        final int getLiveDataTrigger = getLiveDataTriggerCount(getRestaurantInfoUseCase.get(PLACE_ID));

        // THEN
        assertEquals(0, getLiveDataTrigger);
    }
}