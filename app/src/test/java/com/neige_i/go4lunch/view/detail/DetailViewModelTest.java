package com.neige_i.go4lunch.view.detail;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.detail.CleanWorkmate;
import com.neige_i.go4lunch.domain.detail.GetRestaurantInfoUseCase;
import com.neige_i.go4lunch.domain.detail.RestaurantInfo;
import com.neige_i.go4lunch.domain.detail.UpdateRestaurantPrefUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class DetailViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetRestaurantInfoUseCase getRestaurantInfoUseCaseMock = mock(GetRestaurantInfoUseCase.class);
    private final UpdateRestaurantPrefUseCase updateRestaurantPrefUseCaseMock = mock(UpdateRestaurantPrefUseCase.class);
    private final Application applicationMock = mock(Application.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private DetailViewModel detailViewModel;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<RestaurantInfo> restaurantInfoMutableLiveData = new MutableLiveData<>();
    private final String YOU_JOINING = "YOU_JOINING";
    private final String WORKMATE_JOINING = "WORKMATE_JOINING";

    // ------------------------------------------- CONST -------------------------------------------

    private static final String PLACE_ID = "PLACE_ID";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(restaurantInfoMutableLiveData).when(getRestaurantInfoUseCaseMock).get(PLACE_ID);
        doReturn(YOU_JOINING).when(applicationMock).getString(R.string.you_are_joining);
        doReturn(WORKMATE_JOINING).when(applicationMock).getString(R.string.workmate_is_joining, "NAME2");

        // Init ViewModel
        detailViewModel = new DetailViewModel(
            getRestaurantInfoUseCaseMock,
            updateRestaurantPrefUseCaseMock,
            applicationMock
        );
    }

    // ------------------------------------- DEPENDENCY TESTS --------------------------------------

    @Test
    public void returnViewState_when_getValue_with_favoriteAndSelectedAndNoInterestedWorkmate() {
        // GIVEN
        restaurantInfoMutableLiveData.setValue(new RestaurantInfo(
            "NAME",
            "ADDRESS",
            "PHOTO",
            0,
            "+33",
            "https://",
            true, // Favorite
            true, // Selected
            Collections.emptyList() // No interested workmate
        ));

        // WHEN
        final DetailViewState detailViewState = getValueForTesting(detailViewModel.getViewState(PLACE_ID));

        // THEN
        assertEquals(
            new DetailViewState(
                "NAME",
                "PHOTO",
                "ADDRESS",
                0,
                "+33",
                "https://",
                true, // Favorite
                R.drawable.ic_check_on, // "Selected" drawable
                R.color.lime, // "Selected" color
                Collections.emptyList()
            ),
            detailViewState
        );
    }

    @Test
    public void returnViewState_when_getValue_with_notFavoriteAndNotSelectedAnd2InterestedWorkmate() {
        // GIVEN
        restaurantInfoMutableLiveData.setValue(new RestaurantInfo(
            "NAME",
            "ADDRESS",
            "PHOTO",
            0,
            "+33",
            "https://",
            false, // Not favorite
            false, // Not selected
            Arrays.asList(
                new CleanWorkmate("EMAIL1", "NAME1", "PHOTO1", true), // Current user
                new CleanWorkmate("EMAIL2", "NAME2", "PHOTO2", false)
            )
        ));

        // WHEN
        final DetailViewState detailViewState = getValueForTesting(detailViewModel.getViewState(PLACE_ID));

        // THEN
        assertEquals(
            new DetailViewState(
                "NAME",
                "PHOTO",
                "ADDRESS",
                0,
                "+33",
                "https://",
                false, // Not favorite
                R.drawable.ic_check_off, // "Not selected" drawable
                R.color.gray_dark, // "Not selected" color
                Arrays.asList(
                    new WorkmateViewState("EMAIL1", YOU_JOINING, "PHOTO1"), // Current user
                    new WorkmateViewState("EMAIL2", WORKMATE_JOINING, "PHOTO2")
                )
            ),
            detailViewState
        );
    }

    // ------------------------------- RESTAURANT PREFERENCES TESTS --------------------------------

    @Test
    public void likeRestaurant_when_clickOnLikeButton_with_currentlyNotFavorite() {
        // GIVEN
        restaurantInfoMutableLiveData.setValue(new RestaurantInfo(
            "NAME",
            "ADDRESS",
            "PHOTO",
            0,
            "+33",
            "https://",
            false, // Not in favorite
            false,
            Collections.emptyList()
        ));
        getValueForTesting(detailViewModel.getViewState(PLACE_ID));

        // WHEN
        detailViewModel.onLikeButtonClicked(PLACE_ID);

        // THEN
        verify(updateRestaurantPrefUseCaseMock).like(PLACE_ID); // Like it
        verifyNoMoreInteractions(updateRestaurantPrefUseCaseMock);
    }

    @Test
    public void unlikeRestaurant_when_clickOnLikeButton_with_currentlyFavorite() {
        // GIVEN
        restaurantInfoMutableLiveData.setValue(new RestaurantInfo(
            "NAME",
            "ADDRESS",
            "PHOTO",
            0,
            "+33",
            "https://",
            true, // In favorite
            false,
            Collections.emptyList()
        ));
        getValueForTesting(detailViewModel.getViewState(PLACE_ID));

        // WHEN
        detailViewModel.onLikeButtonClicked(PLACE_ID);

        // THEN
        verify(updateRestaurantPrefUseCaseMock).unlike(PLACE_ID); // Unlike it
        verifyNoMoreInteractions(updateRestaurantPrefUseCaseMock);
    }

    @Test
    public void selectRestaurant_when_clickOnSelectButton_with_currentlyNotSelected() {
        // GIVEN
        restaurantInfoMutableLiveData.setValue(new RestaurantInfo(
            "NAME",
            "ADDRESS",
            "PHOTO",
            0,
            "+33",
            "https://",
            false,
            false, // Not selected
            Collections.emptyList()
        ));
        getValueForTesting(detailViewModel.getViewState(PLACE_ID));

        // WHEN
        detailViewModel.onSelectedRestaurantClicked(PLACE_ID);

        // THEN
        verify(updateRestaurantPrefUseCaseMock).select(PLACE_ID, "NAME"); // Select it
        verifyNoMoreInteractions(updateRestaurantPrefUseCaseMock);
    }

    @Test
    public void unselectRestaurant_when_clickOnSelectButton_with_currentlySelected() {
        // GIVEN
        restaurantInfoMutableLiveData.setValue(new RestaurantInfo(
            "NAME",
            "ADDRESS",
            "PHOTO",
            0,
            "+33",
            "https://",
            false,
            true, // Is selected
            Collections.emptyList()
        ));
        getValueForTesting(detailViewModel.getViewState(PLACE_ID));

        // WHEN
        detailViewModel.onSelectedRestaurantClicked(PLACE_ID);

        // THEN
        verify(updateRestaurantPrefUseCaseMock).unselect(); // Unselect it
        verifyNoMoreInteractions(updateRestaurantPrefUseCaseMock);
    }

    // ------------------------------- START EXTERNAL ACTIVITY TESTS -------------------------------

    @Test
    public void startExternalActivity_when_activityIsResolved() {
        // WHEN
        detailViewModel.onExternalActivityAsked(true, "action", "uri");
        final String[] externalActivityEvent = getValueForTesting(detailViewModel.getStartExternalActivityEvent());

        // THEN
        assertArrayEquals(new String[]{"action", "uri"}, externalActivityEvent);
    }

    @Test
    public void doNothing_when_activityIsNotResolved() {
        // WHEN
        detailViewModel.onExternalActivityAsked(false, "action", "uri");
        final int externalActivityEventTrigger = getLiveDataTriggerCount(detailViewModel.getStartExternalActivityEvent());

        // THEN
        assertEquals(0, externalActivityEventTrigger); // Never called
    }
}