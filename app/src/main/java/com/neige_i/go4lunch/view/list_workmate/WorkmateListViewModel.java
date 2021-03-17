package com.neige_i.go4lunch.view.list_workmate;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.firebase.model.User;
import com.neige_i.go4lunch.domain.GetFirestoreUserListUseCase;
import com.neige_i.go4lunch.view.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class WorkmateListViewModel extends ViewModel {

    @NonNull
    private final GetFirestoreUserListUseCase getFirestoreUserListUseCase;

    @NonNull
    private final SingleLiveEvent<String> triggerCallbackEvent = new SingleLiveEvent<>();

    public WorkmateListViewModel(@NonNull GetFirestoreUserListUseCase getFirestoreUserListUseCase) {
        this.getFirestoreUserListUseCase = getFirestoreUserListUseCase;
    }

    @NonNull
    public LiveData<String> getTriggerCallbackEvent() {
        return triggerCallbackEvent;
    }

    @NonNull
    public LiveData<List<WorkmateViewState>> getViewState() {
        return Transformations.map(getFirestoreUserListUseCase.getAllUsers(), userList -> {
            final List<WorkmateViewState> viewStates = new ArrayList<>();

            if (userList != null) {

                for (User user : userList) {
                    final boolean hasUserSelectedARestaurant = user.getSelectedRestaurantId() != null;
                    final String workmateAndRestaurant =
                        hasUserSelectedARestaurant ?
                        user.getName() + " is eating in " + user.getSelectedRestaurantName() :
                        user.getName() + " hasn't decided yet";

                    viewStates.add(new WorkmateViewState(
                        user.getId(),
                        user.getPhotoUrl(),
                        hasUserSelectedARestaurant ? Typeface.NORMAL : Typeface.ITALIC,
                        hasUserSelectedARestaurant ? R.color.black : android.R.color.darker_gray,
                        workmateAndRestaurant,
                        hasUserSelectedARestaurant ? user.getSelectedRestaurantId() : WorkmateViewState.NO_SELECTED_RESTAURANT
                    ));
                }
            }

            return viewStates;
        });
    }

    public void onWorkmateItemClicked(@NonNull String placeId) {
        if (!placeId.equals(WorkmateViewState.NO_SELECTED_RESTAURANT)) {
            triggerCallbackEvent.setValue(placeId);
        }
    }
}
