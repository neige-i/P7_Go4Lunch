package com.neige_i.go4lunch.domain.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetRestaurantInfoUseCaseImpl implements GetRestaurantInfoUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final DetailsRepository detailsRepository;
    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final WorkmatesDelegate workmatesDelegate;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MediatorLiveData<RestaurantInfo> restaurantInfo = new MediatorLiveData<>();

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetRestaurantInfoUseCaseImpl(
        @NonNull DetailsRepository detailsRepository,
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull WorkmatesDelegate workmatesDelegate
    ) {
        this.detailsRepository = detailsRepository;
        this.firestoreRepository = firestoreRepository;
        this.firebaseAuth = firebaseAuth;
        this.workmatesDelegate = workmatesDelegate;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<RestaurantInfo> get(@NonNull String placeId) {
        final LiveData<RestaurantDetails> restaurantDetailsLiveData = detailsRepository.getData(placeId);
        final LiveData<List<User>> interestedWorkmatesLiveData = firestoreRepository.getWorkmatesEatingAt(placeId);

        final LiveData<User> currentUserLiveData;
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserLiveData = firestoreRepository.getUser(firebaseAuth.getCurrentUser().getUid());
        } else {
            currentUserLiveData = new MutableLiveData<>();
        }

        restaurantInfo.addSource(restaurantDetailsLiveData, restaurantDetails -> combine(restaurantDetails, interestedWorkmatesLiveData.getValue(), currentUserLiveData.getValue()));
        restaurantInfo.addSource(interestedWorkmatesLiveData, interestedWorkmates -> combine(restaurantDetailsLiveData.getValue(), interestedWorkmates, currentUserLiveData.getValue()));
        restaurantInfo.addSource(currentUserLiveData, currentUser -> combine(restaurantDetailsLiveData.getValue(), interestedWorkmatesLiveData.getValue(), currentUser));

        return restaurantInfo;
    }

    private void combine(
        @Nullable RestaurantDetails restaurantDetails,
        @Nullable List<User> interestedWorkmates,
        @Nullable User currentUser
    ) {
        if (restaurantDetails == null || currentUser == null) {
            return;
        }

        // Setup favorite property
        final boolean isFavorite = currentUser.getFavoriteRestaurants() != null &&
            currentUser.getFavoriteRestaurants().contains(restaurantDetails.getPlaceId());

        // Setup selected property
        final boolean isSelected = currentUser.getSelectedRestaurant() != null &&
            restaurantDetails.getPlaceId().equals(currentUser.getSelectedRestaurant().getId()) &&
            workmatesDelegate.isSelected(currentUser.getSelectedRestaurant().getDate());

        // Setup interested workmates
        final List<CleanWorkmate> cleanWorkmates = new ArrayList<>();
        if (interestedWorkmates != null) {
            for (User user : interestedWorkmates) {
                cleanWorkmates.add(new CleanWorkmate(
                    user.getEmail(),
                    user.getName(),
                    user.getPhotoUrl(),
                    user.getEmail().equals(currentUser.getEmail())
                ));
            }
        }

        workmatesDelegate.moveToFirstPosition(cleanWorkmates, cleanWorkmate -> cleanWorkmate.isCurrentUser());

        restaurantInfo.setValue(new RestaurantInfo(
            restaurantDetails.getName(),
            restaurantDetails.getAddress(),
            restaurantDetails.getPhotoUrl(),
            restaurantDetails.getRating(),
            restaurantDetails.getPhoneNumber(),
            restaurantDetails.getWebsite(),
            isFavorite,
            isSelected,
            cleanWorkmates
        ));
    }
}
