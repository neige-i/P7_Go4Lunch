package com.neige_i.go4lunch.view.dispatcher;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.domain.dispatcher.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.view.util.SingleLiveEvent;

public class DispatcherViewModel extends ViewModel {

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final SingleLiveEvent<ActivityToStart> startActivityEvent = new SingleLiveEvent<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

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
