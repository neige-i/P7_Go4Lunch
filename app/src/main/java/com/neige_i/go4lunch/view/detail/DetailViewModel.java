package com.neige_i.go4lunch.view.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.detail.CleanWorkmate;
import com.neige_i.go4lunch.domain.detail.GetRestaurantInfoUseCase;
import com.neige_i.go4lunch.domain.detail.UpdateRestaurantPrefUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetailViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetRestaurantInfoUseCase getRestaurantInfoUseCase;
    @NonNull
    private final UpdateRestaurantPrefUseCase updateRestaurantPrefUseCase;
    @NonNull
    private final Application application;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final SingleLiveEvent<String[]> startExternalActivityEvent = new SingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    private boolean isSelected;
    private boolean isFavorite;
    private String restaurantName;
    private String restaurantAddress;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public DetailViewModel(
        @NonNull GetRestaurantInfoUseCase getRestaurantInfoUseCase,
        @NonNull UpdateRestaurantPrefUseCase updateRestaurantPrefUseCase,
        @NonNull Application application
    ) {
        this.getRestaurantInfoUseCase = getRestaurantInfoUseCase;
        this.updateRestaurantPrefUseCase = updateRestaurantPrefUseCase;
        this.application = application;
    }

    // ------------------------------------ VIEW STATE METHODS -------------------------------------

    public LiveData<DetailViewState> getViewState(@NonNull String placeId) {
        return Transformations.map(getRestaurantInfoUseCase.get(placeId), restaurantInfo -> {
            // Update fields
            isFavorite = restaurantInfo.isFavorite();
            isSelected = restaurantInfo.isSelected();
            restaurantName = restaurantInfo.getName();
            restaurantAddress = restaurantInfo.getAddress();

            // Setup interested workmates
            final List<WorkmateViewState> workmateViewStates = new ArrayList<>();
            for (CleanWorkmate cleanWorkmate : restaurantInfo.getInterestedWorkmates()) {
                workmateViewStates.add(new WorkmateViewState(
                    cleanWorkmate.getEmail(),
                    cleanWorkmate.isCurrentUser() ?
                        application.getString(R.string.you_are_joining) :
                        application.getString(R.string.workmate_is_joining, cleanWorkmate.getName()),
                    cleanWorkmate.getPhotoUrl()
                ));
            }

            return new DetailViewState(
                restaurantInfo.getName(),
                restaurantInfo.getPhotoUrl(),
                restaurantInfo.getAddress(),
                restaurantInfo.getRating(),
                restaurantInfo.getPhoneNumber(),
                restaurantInfo.getWebsite(),
                isFavorite,
                isSelected ? R.drawable.ic_check_on : R.drawable.ic_check_off,
                isSelected ? R.color.lime : R.color.gray_dark,
                workmateViewStates
            );
        });
    }

    @NonNull
    public LiveData<String[]> getStartExternalActivityEvent() {
        return startExternalActivityEvent;
    }

    // ------------------------------ RESTAURANT PREFERENCES METHODS -------------------------------

    public void onLikeButtonClicked(@NonNull String placeId) {
        if (isFavorite) {
            updateRestaurantPrefUseCase.unlike(placeId);
        } else {
            updateRestaurantPrefUseCase.like(placeId);
        }
    }

    public void onSelectedRestaurantClicked(@NonNull String placeId) {
        if (isSelected) {
            updateRestaurantPrefUseCase.unselect();
        } else {
            updateRestaurantPrefUseCase.select(placeId, restaurantName, restaurantAddress);
        }
    }

    // ------------------------------------ NAVIGATION METHODS -------------------------------------

    public void onExternalActivityAsked(
        boolean isActivityResolved,
        @NonNull String action,
        @NonNull String uriString
    ) {
        if (isActivityResolved) {
            startExternalActivityEvent.setValue(new String[]{action, uriString});
        }
    }
}
