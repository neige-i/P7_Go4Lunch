package com.neige_i.go4lunch.view.dispatcher;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DispatcherViewModel extends ViewModel {

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final SingleLiveEvent<ActivityToStart> startActivityEvent = new SingleLiveEvent<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public DispatcherViewModel(@NonNull GetFirebaseUserUseCase getFirebaseUserUseCase) {
        startActivityEvent.setValue(
            getFirebaseUserUseCase.getUser() != null ?
                ActivityToStart.HOME_ACTIVITY :
                ActivityToStart.AUTH_ACTIVITY
        );
    }

    @NonNull
    public LiveData<ActivityToStart> getStartActivityEvent() {
        return startActivityEvent;
    }

    // ------------------------------------------- ENUM --------------------------------------------

    enum ActivityToStart {
        HOME_ACTIVITY,
        AUTH_ACTIVITY,
    }
}
