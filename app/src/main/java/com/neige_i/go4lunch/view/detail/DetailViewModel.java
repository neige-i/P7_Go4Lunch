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
import com.neige_i.go4lunch.data.google_places.model.DetailsRestaurant;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsItemUseCase;
import com.neige_i.go4lunch.domain.to_sort.UpdateInterestedWorkmatesUseCase;
import com.neige_i.go4lunch.domain.to_sort.UpdateSelectedRestaurantUseCase;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.domain.model.DetailsModel;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailViewModel extends ViewModel {

    @NonNull
    private final GetRestaurantDetailsItemUseCase getRestaurantDetailsItemUseCase;
    @NonNull
    private final UpdateInterestedWorkmatesUseCase updateInterestedWorkmatesUseCase;
    @NonNull
    private final UpdateSelectedRestaurantUseCase updateSelectedRestaurantUseCase;
    @NonNull
    private final GetFirebaseUserUseCase getFirebaseUserUseCase;
    @NonNull
    private final Clock clock;
    @NonNull
    private final FirestoreRepository firestoreRepository;

    private final MediatorLiveData<DetailViewState> viewState = new MediatorLiveData<>();

    public DetailViewModel(@NonNull GetRestaurantDetailsItemUseCase getRestaurantDetailsItemUseCase,
                           @NonNull UpdateInterestedWorkmatesUseCase updateInterestedWorkmatesUseCase,
                           @NonNull UpdateSelectedRestaurantUseCase updateSelectedRestaurantUseCase,
                           @NonNull GetFirebaseUserUseCase getFirebaseUserUseCase,
                           @NonNull Clock clock,
                           @NonNull FirestoreRepository firestoreRepository
    ) {
        this.getRestaurantDetailsItemUseCase = getRestaurantDetailsItemUseCase;
        this.updateInterestedWorkmatesUseCase = updateInterestedWorkmatesUseCase;
        this.updateSelectedRestaurantUseCase = updateSelectedRestaurantUseCase;
        this.getFirebaseUserUseCase = getFirebaseUserUseCase;
        this.clock = clock;
        this.firestoreRepository = firestoreRepository;
    }

    public LiveData<DetailViewState> getViewState() {
        return viewState;
    }

    public void onInfoQueried(@NonNull String placeId) {
        final LiveData<DetailsModel> detailsModelLiveData = getRestaurantDetailsItemUseCase.getDetailsItem(placeId);
        final LiveData<Restaurant> restaurantLiveData = firestoreRepository.getRestaurant(placeId);

        viewState.addSource(detailsModelLiveData, detailsModel -> {
            combine(detailsModel, restaurantLiveData.getValue());
        });
        viewState.addSource(restaurantLiveData, restaurant -> {
            combine(detailsModelLiveData.getValue(), restaurant);
        });
    }

    private void combine(@Nullable DetailsModel detailsModel, @Nullable Restaurant restaurant) {
        if (detailsModel.getDetailsResponse() == null)
            return;

        final DetailsRestaurant detailsRestaurant = detailsModel.getDetailsResponse();
        final String restaurantId = detailsRestaurant.getPlaceId();

        final List<String> interestedWorkmates = restaurant != null ?
            Collections.singletonList(restaurant.getWorkmateId()) :
            new ArrayList<>();

        // TODO: handle empty field case
        viewState.setValue(new DetailViewState(
            restaurantId,
            detailsRestaurant.getName(),
            detailsRestaurant.getPhotoUrl(),
            detailsRestaurant.getAddress(),
            detailsRestaurant.getRating(),
            detailsRestaurant.getRating() == -1,
            detailsRestaurant.getPhoneNumber(),
            detailsRestaurant.getWebsite(),
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
