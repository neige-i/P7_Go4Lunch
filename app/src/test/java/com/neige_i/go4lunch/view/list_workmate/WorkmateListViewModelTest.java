package com.neige_i.go4lunch.view.list_workmate;

import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.app.Application;
import android.graphics.Typeface;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.list_workmate.GetAllWorkmatesUseCase;
import com.neige_i.go4lunch.domain.list_workmate.Workmate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class WorkmateListViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final GetAllWorkmatesUseCase getAllWorkmatesUseCaseMock = mock(GetAllWorkmatesUseCase.class);
    private final Application applicationMock = mock(Application.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private WorkmateListViewModel workmateListViewModel;

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<List<Workmate>> workmateListMutableLiveData = new MutableLiveData<>();
    private static final String YOU_EATING_AT = "YOU_EATING_AT";
    private static final String WORKMATE_EATING_AT = "WORKMATE_EATING_AT";
    private static final String YOU_NOT_DECIDED = "YOU_NOT_DECIDED";
    private static final String WORKMATE_NOT_DECIDED = "WORKMATE_NOT_DECIDED";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(workmateListMutableLiveData).when(getAllWorkmatesUseCaseMock).get();
        doReturn(YOU_EATING_AT).when(applicationMock).getString(R.string.you_eating_at, "RESTAURANT_NAME");
        doReturn(WORKMATE_EATING_AT).when(applicationMock).getString(R.string.workmate_eating_at, "WORKMATE_NAME", "RESTAURANT_NAME");
        doReturn(YOU_NOT_DECIDED).when(applicationMock).getString(R.string.you_not_decided);
        doReturn(WORKMATE_NOT_DECIDED).when(applicationMock).getString(R.string.workmate_not_decided, "WORKMATE_NAME");

        // Init ViewModel
        workmateListViewModel = new WorkmateListViewModel(getAllWorkmatesUseCaseMock, applicationMock);
    }

    // ------------------------------------- VIEW STATE TESTS --------------------------------------

    @Test
    public void returnViewStates_when_getValue_with_workmateHasChosenButNotCurrentUser() {
        // GIVEN
        workmateListMutableLiveData.setValue(Arrays.asList(
            new Workmate.WithRestaurant("@workmate", "WORKMATE_NAME", "WORKMATE_PHOTO", false, "PLACE_ID", "RESTAURANT_NAME"),
            new Workmate.WithoutRestaurant("@me", "MY_NAME", "MY_PHOTO", true)
        ));

        // WHEN
        final List<WorkmateViewState> workmateViewStates = getValueForTesting(workmateListViewModel.getViewState());

        // THEN
        assertEquals(
            Arrays.asList(
                new WorkmateViewState("@workmate", "WORKMATE_PHOTO", Typeface.NORMAL, R.color.black, WORKMATE_EATING_AT, "PLACE_ID", true),
                new WorkmateViewState("@me", "MY_PHOTO", Typeface.ITALIC, android.R.color.darker_gray, YOU_NOT_DECIDED, null, false)
            ),
            workmateViewStates
        );
    }

    @Test
    public void returnViewStates_when_getValue_with_workmateHasNotChosenButCurrentUserHas() {
        // GIVEN
        workmateListMutableLiveData.setValue(Arrays.asList(
            new Workmate.WithRestaurant("@me", "MY_NAME", "MY_PHOTO", true, "PLACE_ID", "RESTAURANT_NAME"),
            new Workmate.WithoutRestaurant("@workmate", "WORKMATE_NAME", "WORKMATE_PHOTO", false)
        ));

        // WHEN
        final List<WorkmateViewState> workmateViewStates = getValueForTesting(workmateListViewModel.getViewState());

        // THEN
        assertEquals(
            Arrays.asList(
                new WorkmateViewState("@me", "MY_PHOTO", Typeface.NORMAL, R.color.black, YOU_EATING_AT, "PLACE_ID", false),
                new WorkmateViewState("@workmate", "WORKMATE_PHOTO", Typeface.ITALIC, android.R.color.darker_gray, WORKMATE_NOT_DECIDED, null, true)
            ),
            workmateViewStates
        );
    }
}