package com.neige_i.go4lunch.domain;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.domain.model.DetailsModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GetAllRestaurantDetailsUseCaseImplTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Values returned from mocked objects
    private final MutableLiveData<String> selectedRestaurant = new MutableLiveData<>();
    private final MutableLiveData<List<String>> favoriteRestaurants = new MutableLiveData<>();
    private final MutableLiveData<DetailsResponse> detailsResponse = new MutableLiveData<>();

    // Mock objects
    private final DetailsRepository detailsRepository = mock(DetailsRepository.class);
    private final FirebaseRepository firebaseRepository = mock(FirebaseRepository.class);
    private final String placeId = "place ID to search";

    // UseCase under test
    private GetAllRestaurantDetailsUseCase getAllRestaurantDetailsUseCase;

    @Before
    public void setUp() {
        doReturn(selectedRestaurant).when(firebaseRepository).getSelectedRestaurant();
        doReturn(favoriteRestaurants).when(firebaseRepository).getFavoriteRestaurants();
        doReturn(detailsResponse).when(detailsRepository).getDetailsResponse(placeId);

        getAllRestaurantDetailsUseCase = new GetAllRestaurantDetailsUseCaseImpl(detailsRepository, firebaseRepository);
    }

    @Test
    public void getAllDetails_nominalCase() throws InterruptedException {
        // Given
        detailsResponse.setValue(new DetailsResponse());
        selectedRestaurant.setValue("Some place ID");
        favoriteRestaurants.setValue(Arrays.asList("place ID 1", "place ID 2"));

        // Then
        assertEquals(new DetailsModel(
            new DetailsResponse(),
            "Some place ID",
            Arrays.asList("place ID 1", "place ID 2")
        ), getOrAwaitValue(getAllRestaurantDetailsUseCase.getAllDetails(placeId)));
    }

    @Test
    public void getAllDetails_altCase_noResponse_noneSelected_noFavorite() throws InterruptedException {
        // Given
        detailsResponse.setValue(null);
        selectedRestaurant.setValue(null);
        favoriteRestaurants.setValue(Collections.emptyList());

        // Then
        assertEquals(new DetailsModel(
            null,
            null,
            Collections.emptyList()
        ), getOrAwaitValue(getAllRestaurantDetailsUseCase.getAllDetails(placeId)));
    }

    @Test
    public void getAllDetails_edgeCase_selectedListNull() throws InterruptedException {
        // Given
        detailsResponse.setValue(new DetailsResponse());
        selectedRestaurant.setValue("place ID");
        favoriteRestaurants.setValue(null);

        // Then
        assertEquals(new DetailsModel(
            new DetailsResponse(),
            "place ID",
            Collections.emptyList()
        ), getOrAwaitValue(getAllRestaurantDetailsUseCase.getAllDetails(placeId)));
    }

    @Test
    public void getAllDetails_edgeCase_noResponseRetrieved() throws InterruptedException {
        // Given
        // omit setValue for details response
        selectedRestaurant.setValue("fav place ID");
        favoriteRestaurants.setValue(Arrays.asList("placeID 1", "placeID 2"));

        // Then
        assertEquals(new DetailsModel(
            null,
            "fav place ID",
            Arrays.asList("placeID 1", "placeID 2")
        ), getOrAwaitValue(getAllRestaurantDetailsUseCase.getAllDetails(placeId)));
    }

    @Test
    public void getAllDetails_edgeCase_noSelectedRetrieved() throws InterruptedException {
        // Given
        detailsResponse.setValue(new DetailsResponse());
        // omit setValue for selected restaurant
        favoriteRestaurants.setValue(Arrays.asList("placeID #1", "placeID #2"));

        // Then
        assertEquals(new DetailsModel(
            new DetailsResponse(),
            null,
            Arrays.asList("placeID #1", "placeID #2")
        ), getOrAwaitValue(getAllRestaurantDetailsUseCase.getAllDetails(placeId)));
    }

    @Test
    public void getAllDetails_edgeCase_noFavoriteRetrieved() throws InterruptedException {
        // Given
        detailsResponse.setValue(new DetailsResponse());
        selectedRestaurant.setValue("favorite place ID");
        // omit setValue for favorite restaurants

        // Then
        assertEquals(new DetailsModel(
            new DetailsResponse(),
            "favorite place ID",
            Collections.emptyList()
        ), getOrAwaitValue(getAllRestaurantDetailsUseCase.getAllDetails(placeId)));
    }

    @Test
    public void getAllDetails_edgeCase_noLiveDataRetrieved() {
        // Given
        // omit setValue

        // When
        final Throwable thrownException = assertThrows(
            RuntimeException.class,
            () -> getOrAwaitValue(getAllRestaurantDetailsUseCase.getAllDetails(placeId))
        );

        // Then
        assertEquals("LiveData value was never set.", thrownException.getMessage());
    }
}