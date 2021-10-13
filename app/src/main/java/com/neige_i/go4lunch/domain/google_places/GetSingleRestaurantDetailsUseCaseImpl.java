package com.neige_i.go4lunch.domain.google_places;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.domain.model.DetailsModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetSingleRestaurantDetailsUseCaseImpl implements GetSingleRestaurantDetailsUseCase {

    @NonNull
    private final DetailsRepository detailsRepository;
    @NonNull
    private final FirebaseRepository firebaseRepository;

    @NonNull
    private final MediatorLiveData<DetailsModel> detailsModel = new MediatorLiveData<>();

    @Inject
    public GetSingleRestaurantDetailsUseCaseImpl(
        @NonNull DetailsRepository detailsRepository,
        @NonNull FirebaseRepository firebaseRepository
    ) {
        this.detailsRepository = detailsRepository;
        this.firebaseRepository = firebaseRepository;
    }

    @NonNull
    @Override
    public LiveData<DetailsModel> getDetailsItem(@NonNull String placeId) {
        final LiveData<RestaurantDetails> detailsResponseLiveData = detailsRepository.getRestaurantDetails(placeId);
        final LiveData<String> selectedRestaurantLiveData = firebaseRepository.getSelectedRestaurant();
        final LiveData<List<String>> favRestaurantsLiveData = firebaseRepository.getFavoriteRestaurants();

        detailsModel.addSource(detailsResponseLiveData, detailsResponse -> combine(
            detailsResponse,
            selectedRestaurantLiveData.getValue(),
            favRestaurantsLiveData.getValue()
        ));
        detailsModel.addSource(selectedRestaurantLiveData, selectedRestaurant -> combine(
            detailsResponseLiveData.getValue(),
            selectedRestaurant,
            favRestaurantsLiveData.getValue()
        ));
        detailsModel.addSource(favRestaurantsLiveData, favoriteRestaurants -> combine(
            detailsResponseLiveData.getValue(),
            selectedRestaurantLiveData.getValue(),
            favoriteRestaurants
        ));

        return detailsModel;
    }

    private void combine(
        @Nullable RestaurantDetails restaurantDetails,
        @Nullable String selectedRestaurant,
        @NonNull List<String> favoriteRestaurants
    ) {
        final List<String> favorites;
        if (favoriteRestaurants == null) { // Should never happen
            favorites = Collections.emptyList();
        } else {
            favorites = favoriteRestaurants;
        }

        detailsModel.setValue(new DetailsModel(restaurantDetails, selectedRestaurant, favorites));
    }
}
