package com.neige_i.go4lunch.view.list_workmate;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.firebase.model.User;
import com.neige_i.go4lunch.domain.to_sort.GetFirestoreUserListUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmateListViewModel extends ViewModel {

    @NonNull
    private final GetFirestoreUserListUseCase getFirestoreUserListUseCase;
    @NonNull
    private final Clock clock;

    @NonNull
    private final SingleLiveEvent<String> triggerCallbackEvent = new SingleLiveEvent<>();

    @Inject
    public WorkmateListViewModel(@NonNull GetFirestoreUserListUseCase getFirestoreUserListUseCase, @NonNull Clock clock) {
        this.getFirestoreUserListUseCase = getFirestoreUserListUseCase;
        this.clock = clock;
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

                    final User.SelectedRestaurant selectedRestaurant = user.getSelectedRestaurant();

                    // Check if user has selected a restaurant FOR TODAY
                    final boolean hasUserSelectedARestaurant;
                    if (selectedRestaurant != null) {

                        final LocalDate selectedLocalDate = Instant
                            .ofEpochMilli(selectedRestaurant.getSelectedDate())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                        hasUserSelectedARestaurant = selectedLocalDate.isEqual(LocalDate.now(clock));
                    } else {
                        hasUserSelectedARestaurant = false;
                    }

                    // Set the WorkmateViewState fields
                    final int textStyle;
                    final int textColor;
                    final String nameAndSelectedRestaurant;
                    final String selectedRestaurantId;

                    if (hasUserSelectedARestaurant) {
                        textStyle = Typeface.NORMAL;
                        textColor = R.color.black;
                        nameAndSelectedRestaurant = user.getName() + " is eating in " + selectedRestaurant.getRestaurantName();
                        selectedRestaurantId = selectedRestaurant.getRestaurantId();
                    } else {
                        textStyle = Typeface.ITALIC;
                        textColor = android.R.color.darker_gray;
                        nameAndSelectedRestaurant = user.getName() + " hasn't decided yet";
                        selectedRestaurantId = WorkmateViewState.NO_SELECTED_RESTAURANT;
                    }

                    // Add the view state to the list
                    viewStates.add(new WorkmateViewState(
                        user.getEmail(),
                        user.getPhotoUrl(),
                        textStyle,
                        textColor,
                        nameAndSelectedRestaurant,
                        selectedRestaurantId
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
