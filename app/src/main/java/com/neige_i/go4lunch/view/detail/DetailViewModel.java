package com.neige_i.go4lunch.view.detail;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.firebase.model.User;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.domain.model.DetailsModel;
import com.neige_i.go4lunch.domain.google_places.GetSingleRestaurantDetailsUseCase;
import com.neige_i.go4lunch.domain.to_sort.UpdateSelectedRestaurantUseCase;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetailViewModel extends ViewModel {

    @NonNull
    private final GetSingleRestaurantDetailsUseCase getSingleRestaurantDetailsUseCase;
    @NonNull
    private final UpdateSelectedRestaurantUseCase updateSelectedRestaurantUseCase;
    @NonNull
    private final GetFirebaseUserUseCase getFirebaseUserUseCase;
    @NonNull
    private final Clock clock;
    @NonNull
    private final FirestoreRepository firestoreRepository;

    private final MediatorLiveData<DetailViewState> viewState = new MediatorLiveData<>();

    @Inject
    public DetailViewModel(
        @NonNull GetSingleRestaurantDetailsUseCase getSingleRestaurantDetailsUseCase,
        @NonNull UpdateSelectedRestaurantUseCase updateSelectedRestaurantUseCase,
        @NonNull GetFirebaseUserUseCase getFirebaseUserUseCase,
        @NonNull Clock clock,
        @NonNull FirestoreRepository firestoreRepository
    ) {
        this.getSingleRestaurantDetailsUseCase = getSingleRestaurantDetailsUseCase;
        this.updateSelectedRestaurantUseCase = updateSelectedRestaurantUseCase;
        this.getFirebaseUserUseCase = getFirebaseUserUseCase;
        this.clock = clock;
        this.firestoreRepository = firestoreRepository;
    }

    public LiveData<DetailViewState> getViewState() {
        return viewState;
    }

    public void onInfoQueried(@NonNull String placeId) {
        final LiveData<DetailsModel> detailsModelLiveData = getSingleRestaurantDetailsUseCase.getDetailsItem(placeId);
        final LiveData<Restaurant> restaurantLiveData = firestoreRepository.getRestaurantById(placeId);

        viewState.addSource(detailsModelLiveData, detailsModel -> {
            combine(detailsModel, restaurantLiveData.getValue());
        });
        viewState.addSource(restaurantLiveData, restaurant -> {
            combine(detailsModelLiveData.getValue(), restaurant);
        });
    }

    private void combine(@Nullable DetailsModel detailsModel, @Nullable Restaurant restaurant) {
        if (detailsModel.getDetailsResponse() == null) {
            return;
        }

        final RestaurantDetails restaurantDetails = detailsModel.getDetailsResponse();
        final String restaurantId = restaurantDetails.getPlaceId();

        final List<String> interestedWorkmates = restaurant != null ?
            Collections.singletonList("") :
            new ArrayList<>();

        // TODO: handle empty field case
        viewState.setValue(new DetailViewState(
            restaurantId,
            restaurantDetails.getName(),
            restaurantDetails.getPhotoUrl(),
            restaurantDetails.getAddress(),
            restaurantDetails.getRating(),
            restaurantDetails.getRating() == -1,
            restaurantDetails.getPhoneNumber(),
            restaurantDetails.getWebsite(),
            restaurantId.equals(detailsModel.getSelectedRestaurant()),
            detailsModel.getFavoriteRestaurants().contains(restaurantId),
            interestedWorkmates
        ));
    }

    public void onLikeBtnClicked() {
        final DetailViewState currentViewState = viewState.getValue();
        if (currentViewState != null) {
//            toggleFavoriteRestaurant.toggleFavorite(currentViewState.getPlaceId());
        }
    }

    public void onSelectedRestaurantUpdated(boolean isSelected) {
        final DetailViewState currentViewState = viewState.getValue();
        if (currentViewState != null) {
            final String userId = getFirebaseUserUseCase.getUser().getUid();

            if (isSelected) {
                updateSelectedRestaurantUseCase.selectRestaurant(
                    userId,
                    new User.SelectedRestaurant(
                        currentViewState.getPlaceId(),
                        currentViewState.getName(),
                        LocalDate.now(clock).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    )
                );
                Log.d("Neige", "DetailViewModel::onSelectedRestaurantUpdated");
                firestoreRepository.addInterestedWorkmate(currentViewState.getPlaceId(), userId);
//                updateInterestedWorkmatesUseCase.addWorkmateToList();
            } else {
                updateSelectedRestaurantUseCase.clearRestaurant(userId);
            }
        }
    }
}
