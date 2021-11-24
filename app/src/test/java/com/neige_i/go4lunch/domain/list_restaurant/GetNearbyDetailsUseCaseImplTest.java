package com.neige_i.go4lunch.domain.list_restaurant;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.data.location.LocationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetNearbyDetailsUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final LocationRepository locationRepositoryMock = mock(LocationRepository.class);
    private final NearbyRepository nearbyRepositoryMock = mock(NearbyRepository.class);
    private final DetailsRepository detailsRepositoryMock = mock(DetailsRepository.class);
    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);
    private final Clock clock_17_11_2021_09_15 = Clock.fixed(
        LocalDateTime.of(2021, 11, 17, 9, 15).atZone(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );
    private final Location restaurantLocationMock = mock(Location.class);

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Location deviceLocationMock = mock(Location.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private GetNearbyDetailsUseCase getNearbyDetailsUseCase;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<NearbyRestaurant>> nearbyRestaurantsMutableLiveData = new MutableLiveData<>();
    private final List<MutableLiveData<RestaurantDetails>> restaurantDetailsMutableLiveDataList = new ArrayList<>();
    private final List<MutableLiveData<List<User>>> interestedWorkmatesMutableLiveDataList = new ArrayList<>();

    // ----------------------------------------- CONSTANT ------------------------------------------

    private static final String PLACE_ID = "PLACE_ID";
    private static final float RESTAURANT_DISTANCE = 100;
    private static final int RESTAURANT_COUNT = 7;

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() throws Exception {
        // Setup mocks
        doReturn(RESTAURANT_DISTANCE).when(deviceLocationMock).distanceTo(restaurantLocationMock);
        doReturn(locationMutableLiveData).when(locationRepositoryMock).getCurrentLocation();
        doReturn(nearbyRestaurantsMutableLiveData).when(nearbyRepositoryMock).getData(deviceLocationMock);
        initRestaurantDetailsAndInterestedWorkmatesMock();

        // Init UseCase
        getNearbyDetailsUseCase = new GetNearbyDetailsUseCaseImpl(
            locationRepositoryMock,
            nearbyRepositoryMock,
            detailsRepositoryMock,
            firestoreRepositoryMock,
            clock_17_11_2021_09_15,
            restaurantLocationMock
        );

        // Default behaviour
        locationMutableLiveData.setValue(deviceLocationMock);
        nearbyRestaurantsMutableLiveData.setValue(getDefaultNearbyRestaurantList());
        interestedWorkmatesMutableLiveDataList.get(0).setValue(Collections.emptyList());
        interestedWorkmatesMutableLiveDataList.get(1).setValue(Collections.nCopies(1, new User()));
        interestedWorkmatesMutableLiveDataList.get(2).setValue(Collections.nCopies(5, new User()));
    }

    private void initRestaurantDetailsAndInterestedWorkmatesMock() {
        for (int i = 0; i < RESTAURANT_COUNT; i++) {
            final MutableLiveData<RestaurantDetails> restaurantDetails = new MutableLiveData<>();
            final MutableLiveData<List<User>> interestedWorkmates = new MutableLiveData<>();

            restaurantDetailsMutableLiveDataList.add(restaurantDetails);
            interestedWorkmatesMutableLiveDataList.add(interestedWorkmates);

            doReturn(restaurantDetails).when(detailsRepositoryMock).getData(PLACE_ID + i);
            doReturn(interestedWorkmates).when(firestoreRepositoryMock).getWorkmatesEatingAt(PLACE_ID + i);
        }
    }

    @Test
    public void returnNearbyDetailList_when_getValue() {
        // GIVEN
        // Do not set restaurantDetailsMutableLiveDataList[0]
        restaurantDetailsMutableLiveDataList.get(1).setValue(getDefaultRestaurantDetails(
            Collections.emptyList()
        ));
        restaurantDetailsMutableLiveDataList.get(2).setValue(getDefaultRestaurantDetails(
            Collections.singletonList(
                new RestaurantDetails.RestaurantHour(true, DayOfWeek.SUNDAY, LocalTime.of(0, 0))
            )
        ));
        restaurantDetailsMutableLiveDataList.get(3).setValue(getDefaultRestaurantDetails(
            Arrays.asList(
                new RestaurantDetails.RestaurantHour(true, DayOfWeek.MONDAY, LocalTime.of(10, 30)),
                new RestaurantDetails.RestaurantHour(false, DayOfWeek.MONDAY, LocalTime.of(12, 15))
            )
        ));
        restaurantDetailsMutableLiveDataList.get(4).setValue(getDefaultRestaurantDetails(
            Arrays.asList(
                new RestaurantDetails.RestaurantHour(true, DayOfWeek.TUESDAY, LocalTime.of(10, 30)),
                new RestaurantDetails.RestaurantHour(false, DayOfWeek.WEDNESDAY, LocalTime.of(9, 30))
            )
        ));
        restaurantDetailsMutableLiveDataList.get(5).setValue(getDefaultRestaurantDetails(
            Arrays.asList(
                new RestaurantDetails.RestaurantHour(true, DayOfWeek.WEDNESDAY, LocalTime.of(9, 15)),
                new RestaurantDetails.RestaurantHour(false, DayOfWeek.WEDNESDAY, LocalTime.of(19, 30))
            )
        ));
        restaurantDetailsMutableLiveDataList.get(6).setValue(getDefaultRestaurantDetails(
            Collections.singletonList(
                new RestaurantDetails.RestaurantHour(false, DayOfWeek.SATURDAY, LocalTime.of(20, 0)) // Strange hour
            )
        ));

        // WHEN
        final List<NearbyDetail> nearbyDetailList = getValueForTesting(getNearbyDetailsUseCase.get());

        // THEN
        final LocalDate localDate = LocalDate.now(clock_17_11_2021_09_15); // Wednesday
        assertEquals(
            Arrays.asList(
                getDefaultNearbyDetail(0, new HourResult.Loading(), 0),
                getDefaultNearbyDetail(1, new HourResult.Unknown(), 1),
                getDefaultNearbyDetail(2, new HourResult.AlwaysOpen(), 5),
                getDefaultNearbyDetail(3, new HourResult.Closed(5, localDate.plusDays(5).atTime(10, 30)), 0),
                getDefaultNearbyDetail(4, new HourResult.ClosingSoon(), 0),
                getDefaultNearbyDetail(5, new HourResult.Open(0, localDate.atTime(19, 30)), 0),
                getDefaultNearbyDetail(6, new HourResult.Open(3, localDate.plusDays(3).atTime(20, 0)), 0)
            ),
            nearbyDetailList
        );
    }

    @Test
    public void doNothing_when_getValue_with_unavailableLocation() {
        // GIVEN
        locationMutableLiveData.setValue(null);

        // WHEN
        final int nearbyDetailsTrigger = getLiveDataTriggerCount(getNearbyDetailsUseCase.get());

        // THEN
        assertEquals(0, nearbyDetailsTrigger);
    }

    @Test
    public void doNothing_when_getValue_with_unavailableNearbyRestaurants() {
        // GIVEN
        nearbyRestaurantsMutableLiveData.setValue(null);

        // WHEN
        final int nearbyDetailsTrigger = getLiveDataTriggerCount(getNearbyDetailsUseCase.get());

        // THEN
        assertEquals(0, nearbyDetailsTrigger);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private RestaurantDetails getDefaultRestaurantDetails(@NonNull List<RestaurantDetails.RestaurantHour> restaurantHours) {
        return new RestaurantDetails(
            "place ID",
            "name",
            "address",
            0,
            null,
            null,
            null,
            restaurantHours
        );
    }

    private List<NearbyRestaurant> getDefaultNearbyRestaurantList() {
        final List<NearbyRestaurant> nearbyRestaurantList = new ArrayList<>();
        for (int i = 0; i < RESTAURANT_COUNT; i++) {
            nearbyRestaurantList.add(getDefaultNearbyRestaurant(i));
        }
        return nearbyRestaurantList;
    }

    @NonNull
    private NearbyRestaurant getDefaultNearbyRestaurant(int index) {
        return new NearbyRestaurant(
            PLACE_ID + index,
            "RESTAURANT_NAME" + index,
            "RESTAURANT_ADDRESS" + index,
            0,
            0,
            index,
            "RESTAURANT_PHOTO" + index
        );
    }

    @NonNull
    private NearbyDetail getDefaultNearbyDetail(
        int index,
        @NonNull HourResult hourResult,
        int interestedWorkmateCount
    ) {
        return new NearbyDetail(
            PLACE_ID + index,
            "RESTAURANT_NAME" + index,
            "RESTAURANT_ADDRESS" + index,
            RESTAURANT_DISTANCE,
            hourResult,
            interestedWorkmateCount,
            index,
            "RESTAURANT_PHOTO" + index
        );
    }
}