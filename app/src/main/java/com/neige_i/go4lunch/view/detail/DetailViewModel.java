package com.neige_i.go4lunch.view.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.domain.GetRestaurantDetailsItemUseCase;
import com.neige_i.go4lunch.domain.ToggleFavRestaurantUseCase;
import com.neige_i.go4lunch.domain.UpdateSelectedRestaurantUseCase;
import com.neige_i.go4lunch.view.util.Util;

public class DetailViewModel extends ViewModel {

    @NonNull
    private final GetRestaurantDetailsItemUseCase getRestaurantDetailsItemUseCase;
    @NonNull
    private final ToggleFavRestaurantUseCase toggleFavoriteRestaurant;
    @NonNull
    private final UpdateSelectedRestaurantUseCase updateSelectedRestaurantUseCase;

    private final MediatorLiveData<DetailViewState> viewState = new MediatorLiveData<>();

    public DetailViewModel(@NonNull GetRestaurantDetailsItemUseCase getRestaurantDetailsItemUseCase, @NonNull ToggleFavRestaurantUseCase toggleFavoriteRestaurant, @NonNull UpdateSelectedRestaurantUseCase updateSelectedRestaurantUseCase) {
        this.getRestaurantDetailsItemUseCase = getRestaurantDetailsItemUseCase;
        this.toggleFavoriteRestaurant = toggleFavoriteRestaurant;
        this.updateSelectedRestaurantUseCase = updateSelectedRestaurantUseCase;
    }

    public LiveData<DetailViewState> getViewState() {
        return viewState;
    }

    public void onInfoQueried(@NonNull String placeId) {
        viewState.addSource(getRestaurantDetailsItemUseCase.getDetailsItem(placeId), detailsModel -> {
            if (detailsModel.getDetailsResponse() == null)
                return;

            final DetailsResponse.Result result = detailsModel.getDetailsResponse().getResult();

            // TODO: handle empty field case
            viewState.setValue(new DetailViewState(
                placeId,
                result.getName(),
                Util.getPhotoUrl(result.getPhotos()),
                Util.getShortAddress(result.getFormattedAddress()),
                Util.getRating(result.getRating()),
                result.getInternationalPhoneNumber(),
                result.getWebsite(),
                placeId.equals(detailsModel.getSelectedRestaurant()),
                detailsModel.getFavoriteRestaurants().contains(placeId)
            ));
        });
    }

    public void onLikeBtnClicked() {
        final DetailViewState currentViewState = viewState.getValue();
        if (currentViewState != null) {
            toggleFavoriteRestaurant.toggleFavorite(currentViewState.getPlaceId());
        }
    }

    public void onSelectedRestaurantUpdated(boolean isSelected) {
        final DetailViewState currentViewState = viewState.getValue();
        if (currentViewState != null) {
            if (isSelected)
                updateSelectedRestaurantUseCase.setSelectedRestaurant(currentViewState.getPlaceId());
            else
                updateSelectedRestaurantUseCase.clearSelectedRestaurant();
        }
    }
}
