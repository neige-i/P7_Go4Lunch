package com.neige_i.go4lunch.view.list_restaurant;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.app.Application;
import android.graphics.Typeface;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.list_restaurant.GetNearbyDetailsUseCase;
import com.neige_i.go4lunch.domain.list_restaurant.HourResult;
import com.neige_i.go4lunch.domain.list_restaurant.NearbyDetail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RestaurantListViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetNearbyDetailsUseCase getNearbyDetailsUseCaseMock = mock(GetNearbyDetailsUseCase.class);
    private final Application applicationMock = mock(Application.class);
    private final Locale defaultLocale = Locale.FRANCE;

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private RestaurantListViewModel restaurantListViewModel;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<List<NearbyDetail>> nearbyDetailListMutableLiveData = new MutableLiveData<>();
    private final String HOURS_UNKNOWN = "unknown";
    private final String HOURS_ALWAYS_OPEN = "always open";
    private final String HOURS_CLOSING_SOON = "closing soon";
    private final String HOURS_OPEN_UNTIL_TODAY = "open until 10:30";
    private final String HOURS_OPEN_UNTIL_TOMORROW = "open until tomorrow 10:30";
    private final String HOURS_OPEN_UNTIL = "open until lun. 10:30";
    private final String HOURS_CLOSED_UNTIL = "closed until lun. 10:30";
    private final LocalDateTime NEXT_OPENING_HOUR = LocalDateTime.of(2021, 11, 15, 10, 30); // Monday

    // ---------------------------------- RESTAURANT FIELD VALUES ----------------------------------

    private static final String EXPECTED_PLACE_ID = "EXPECTED_PLACE_ID";
    private static final String EXPECTED_NAME = "EXPECTED_NAME";
    private static final String EXPECTED_ADDRESS = "EXPECTED_ADDRESS";
    private static final int EXPECTED_WORKMATES_COUNT = 0;
    private static final int EXPECTED_RATING = 0;
    private static final String EXPECTED_PHOTO = "EXPECTED_PHOTO";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(nearbyDetailListMutableLiveData).when(getNearbyDetailsUseCaseMock).get();
        doReturn(HOURS_UNKNOWN).when(applicationMock).getString(R.string.unknown_hours);
        doReturn(HOURS_ALWAYS_OPEN).when(applicationMock).getString(R.string.always_open);
        doReturn(HOURS_CLOSING_SOON).when(applicationMock).getString(R.string.closing_soon);
        doReturn("tomorrow").when(applicationMock).getString(R.string.tomorrow);
        final LocalTime openingHourTime = NEXT_OPENING_HOUR.toLocalTime();
        doReturn(HOURS_OPEN_UNTIL_TODAY).when(applicationMock).getString(R.string.open_until, "", openingHourTime);
        doReturn(HOURS_OPEN_UNTIL_TOMORROW).when(applicationMock).getString(R.string.open_until, "tomorrow ", openingHourTime);
        doReturn(HOURS_OPEN_UNTIL).when(applicationMock).getString(R.string.open_until, "lun. ", openingHourTime);
        doReturn(HOURS_CLOSED_UNTIL).when(applicationMock).getString(R.string.closed_until, "lun. ", openingHourTime);

        // Init ViewModel
        restaurantListViewModel = new RestaurantListViewModel(getNearbyDetailsUseCaseMock, applicationMock, defaultLocale);
    }

    // ------------------------------------- VIEW STATE TESTS --------------------------------------

    @Test
    public void returnViewState() throws InterruptedException {
        // GIVEN
        nearbyDetailListMutableLiveData.setValue(Arrays.asList(
            restaurant_15987m_closedUntilAfterTomorrow,
            restaurant_1000m_closingSoon,
            restaurant_50m_loadingHours,
            restaurant_1000m_openToday,
            restaurant_500m_unknownHours,
            restaurant_5489m_openAfterTomorrow,
            restaurant_1500m_openTomorrow,
            restaurant_7834m_closedUntilAfterTomorrow,
            restaurant_999m_alwaysOpen
        ));

        // WHEN
        final List<RestaurantViewState> restaurantViewStates = getOrAwaitValue(restaurantListViewModel.getViewState());

        // THEN
        Assert.assertEquals(
            Arrays.asList(
                viewState_50m_loadingHours,
                viewState_500m_unknownHours,
                viewState_999m_alwaysOpen,
                viewState_1000m_closingSoon,
                viewState_1000m_openToday,
                viewState_1500m_openTomorrow,
                viewState_5489m_openAfterTomorrow,
                viewState_7834m_closedUntilAfterTomorrow,
                viewState_15987m_closedUntilAfterTomorrow
            ),
            restaurantViewStates
        );
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private final NearbyDetail restaurant_15987m_closedUntilAfterTomorrow =
        getDefaultNearbyDetail(1, 15987, new HourResult.Closed(4, NEXT_OPENING_HOUR));
    @NonNull
    private final RestaurantViewState viewState_15987m_closedUntilAfterTomorrow =
        getDefaultRestaurantViewState(1, 15987, "15,99km", HOURS_CLOSED_UNTIL, Typeface.BOLD, android.R.color.holo_red_dark);
    @NonNull
    private final NearbyDetail restaurant_1000m_closingSoon =
        getDefaultNearbyDetail(2, 1000, new HourResult.ClosingSoon());
    @NonNull
    private final RestaurantViewState viewState_1000m_closingSoon =
        getDefaultRestaurantViewState(2, 1000, "1,00km", HOURS_CLOSING_SOON, Typeface.BOLD, android.R.color.holo_red_dark);
    @NonNull
    private final NearbyDetail restaurant_50m_loadingHours =
        getDefaultNearbyDetail(3, 50.43f, new HourResult.Loading());
    @NonNull
    private final RestaurantViewState viewState_50m_loadingHours =
        getDefaultRestaurantViewState(3, 50.43f, "50m", "...", Typeface.NORMAL, R.color.gray_dark);
    @NonNull
    private final NearbyDetail restaurant_1000m_openToday =
        getDefaultNearbyDetail(4, 1000.76f, new HourResult.Open(0, NEXT_OPENING_HOUR));
    @NonNull
    private final RestaurantViewState viewState_1000m_openToday =
        getDefaultRestaurantViewState(4, 1000.76f, "1,00km", HOURS_OPEN_UNTIL_TODAY, Typeface.ITALIC, R.color.lime_dark);
    @NonNull
    private final NearbyDetail restaurant_500m_unknownHours =
        getDefaultNearbyDetail(5, 500, new HourResult.Unknown());
    @NonNull
    private final RestaurantViewState viewState_500m_unknownHours =
        getDefaultRestaurantViewState(5, 500, "500m", HOURS_UNKNOWN, Typeface.NORMAL, R.color.gray_dark);
    @NonNull
    private final NearbyDetail restaurant_5489m_openAfterTomorrow =
        getDefaultNearbyDetail(6, 5489.12f, new HourResult.Open(2, NEXT_OPENING_HOUR));
    @NonNull
    private final RestaurantViewState viewState_5489m_openAfterTomorrow =
        getDefaultRestaurantViewState(6, 5489.12f, "5,49km", HOURS_OPEN_UNTIL, Typeface.ITALIC, R.color.lime_dark);
    @NonNull
    private final NearbyDetail restaurant_1500m_openTomorrow =
        getDefaultNearbyDetail(7, 1500, new HourResult.Open(1, NEXT_OPENING_HOUR));
    @NonNull
    private final RestaurantViewState viewState_1500m_openTomorrow =
        getDefaultRestaurantViewState(7, 1500, "1,50km", HOURS_OPEN_UNTIL_TOMORROW, Typeface.ITALIC, R.color.lime_dark);
    @NonNull
    private final NearbyDetail restaurant_7834m_closedUntilAfterTomorrow =
        getDefaultNearbyDetail(8, 7834, new HourResult.Closed(3, NEXT_OPENING_HOUR));
    @NonNull
    private final RestaurantViewState viewState_7834m_closedUntilAfterTomorrow =
        getDefaultRestaurantViewState(8, 7834, "7,83km", HOURS_CLOSED_UNTIL, Typeface.BOLD, android.R.color.holo_red_dark);
    @NonNull
    private final NearbyDetail restaurant_999m_alwaysOpen =
        getDefaultNearbyDetail(9, 999, new HourResult.AlwaysOpen());
    @NonNull
    private final RestaurantViewState viewState_999m_alwaysOpen =
        getDefaultRestaurantViewState(9, 999, "999m", HOURS_ALWAYS_OPEN, Typeface.ITALIC, R.color.lime_dark);
    // --------------------------------------- UTIL METHODS ----------------------------------------

    @NonNull
    private NearbyDetail getDefaultNearbyDetail(
        int index,
        float distance,
        @NonNull HourResult hourResult
    ) {
        return new NearbyDetail(
            EXPECTED_PLACE_ID + index,
            EXPECTED_NAME + index,
            EXPECTED_ADDRESS + index,
            distance,
            hourResult,
            EXPECTED_WORKMATES_COUNT + index,
            EXPECTED_RATING + index,
            EXPECTED_PHOTO + index
        );
    }

    @NonNull
    private RestaurantViewState getDefaultRestaurantViewState(
        int index,
        float distance,
        @NonNull String formattedDistance,
        @NonNull String openingHours,
        int textStyle,
        @ColorRes int textColor
    ) {
        return new RestaurantViewState(
            EXPECTED_PLACE_ID + index,
            EXPECTED_NAME + index,
            EXPECTED_ADDRESS + index,
            distance,
            formattedDistance,
            openingHours,
            textStyle,
            textColor,
            EXPECTED_WORKMATES_COUNT + index,
            EXPECTED_RATING + index,
            EXPECTED_PHOTO + index
        );
    }
}