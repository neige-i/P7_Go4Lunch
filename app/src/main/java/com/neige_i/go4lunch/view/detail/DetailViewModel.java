package com.neige_i.go4lunch.view.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.firebase.FirebaseRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.PlacesResponse;
import com.neige_i.go4lunch.view.util.Util;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private final DetailsRepository detailsRepository;
    private final FirebaseRepository firebaseRepository;

    private final MediatorLiveData<DetailViewState> viewState = new MediatorLiveData<>();

    public DetailViewModel(DetailsRepository detailsRepository, FirebaseRepository firebaseRepository) {
        this.detailsRepository = detailsRepository;
        this.firebaseRepository = firebaseRepository;
    }

    public LiveData<DetailViewState> getViewState() {
        return viewState;
    }

    public void onInfoQueried(@NonNull String placeId) {
        final LiveData<DetailsResponse> detailsResponseLiveData = detailsRepository.getDetailsResponse(placeId);
        final LiveData<List<String>> favoriteRestaurantsLiveData = firebaseRepository.getFavoriteRestaurants();
        final LiveData<String> selectedRestaurantLiveData = firebaseRepository.getSelectedRestaurant();

        viewState.addSource(
            detailsResponseLiveData,
            detailsResponse -> combine(detailsResponse, selectedRestaurantLiveData.getValue(), favoriteRestaurantsLiveData.getValue())
        );
        viewState.addSource(
            firebaseRepository.getSelectedRestaurant(),
            selectedRestaurant -> combine(detailsResponseLiveData.getValue(), selectedRestaurant, favoriteRestaurantsLiveData.getValue())
        );
        viewState.addSource(
            firebaseRepository.getFavoriteRestaurants(),
            favoriteRestaurants -> combine(detailsResponseLiveData.getValue(), selectedRestaurantLiveData.getValue(), favoriteRestaurants)
        );
    }

    private void combine(@Nullable PlacesResponse detailsResponse, @Nullable String selectedRestaurant, @Nullable List<String> favoriteRestaurants) {
        if (detailsResponse == null || favoriteRestaurants == null)
            return;

        final DetailsResponse.Result result = ((DetailsResponse) detailsResponse).getResult();
        final String placeId = result.getPlaceId();

        // TODO: handle empty field case
        viewState.setValue(new DetailViewState(
            placeId,
            result.getName(),
            Util.getPhotoUrl(result.getPhotos()),
            Util.getShortAddress(result.getFormattedAddress()),
            Util.getRating(result.getRating()),
            result.getInternationalPhoneNumber(),
            result.getWebsite(),
            placeId.equals(selectedRestaurant),
            favoriteRestaurants.contains(placeId)
        ));
    }

    public void onLikeBtnClicked() {
        final DetailViewState currentViewState = viewState.getValue();
        if (currentViewState != null) {
            firebaseRepository.toggleFavoriteRestaurant(currentViewState.getPlaceId());
        }
    }

    public void onSelectedRestaurantUpdated(boolean isSelected) {
        final DetailViewState currentViewState = viewState.getValue();
        if (currentViewState != null) {
            if (isSelected)
                firebaseRepository.setSelectedRestaurant(currentViewState.getPlaceId());
            else
                firebaseRepository.clearSelectedRestaurant();
        }
    }
}
