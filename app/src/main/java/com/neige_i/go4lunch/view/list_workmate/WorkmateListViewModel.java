package com.neige_i.go4lunch.view.list_workmate;

import android.app.Application;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.list_workmate.GetAllWorkmatesUseCase;
import com.neige_i.go4lunch.domain.list_workmate.Workmate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmateListViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetAllWorkmatesUseCase getAllWorkmatesUseCase;
    @NonNull
    private final Application application;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public WorkmateListViewModel(
        @NonNull GetAllWorkmatesUseCase getAllWorkmatesUseCase,
        @NonNull Application application
    ) {
        this.getAllWorkmatesUseCase = getAllWorkmatesUseCase;
        this.application = application;
    }

    // ------------------------------------ VIEW STATE METHODS -------------------------------------

    @NonNull
    public LiveData<List<WorkmateViewState>> getViewState() {
        return Transformations.map(getAllWorkmatesUseCase.get(), workmates -> {
            final List<WorkmateViewState> viewStates = new ArrayList<>();

            for (Workmate workmate : workmates) {
                final int textStyle;
                final int textColor;
                final String nameAndSelectedRestaurant;
                final String selectedRestaurantId;

                if (workmate instanceof Workmate.WithRestaurant) {
                    if (workmate.isCurrentUser()) {
                        nameAndSelectedRestaurant = application.getString(R.string.you_eating_at, ((Workmate.WithRestaurant) workmate).getRestaurantName());
                    } else {
                        nameAndSelectedRestaurant = application.getString(
                            R.string.workmate_eating_at,
                            workmate.getName(),
                            ((Workmate.WithRestaurant) workmate).getRestaurantName()
                        );
                    }
                    textStyle = Typeface.NORMAL;
                    textColor = R.color.black;
                    selectedRestaurantId = ((Workmate.WithRestaurant) workmate).getRestaurantId();
                } else {
                    if (workmate.isCurrentUser()) {
                        nameAndSelectedRestaurant = application.getString(R.string.you_not_decided);
                    } else {
                        nameAndSelectedRestaurant = application.getString(
                            R.string.workmate_not_decided,
                            workmate.getName()
                        );
                    }
                    textStyle = Typeface.ITALIC;
                    textColor = android.R.color.darker_gray;
                    selectedRestaurantId = null;
                }

                // Add the view state to the list
                viewStates.add(new WorkmateViewState(
                    workmate.getEmail(),
                    workmate.getPhotoUrl(),
                    textStyle,
                    textColor,
                    nameAndSelectedRestaurant,
                    selectedRestaurantId,
                    !workmate.isCurrentUser()
                ));
            }

            return viewStates;
        });
    }
}
