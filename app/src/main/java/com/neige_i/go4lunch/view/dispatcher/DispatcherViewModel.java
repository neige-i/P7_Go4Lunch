package com.neige_i.go4lunch.view.dispatcher;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class DispatcherViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetAuthUseCase getAuthUseCase;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    DispatcherViewModel(@NonNull GetAuthUseCase getAuthUseCase) {
        this.getAuthUseCase = getAuthUseCase;
    }

    @NonNull
    LiveData<ActivityToStart> getStartActivityEvent() {
        final SingleLiveEvent<ActivityToStart> startActivityEvent = new SingleLiveEvent<>();

        startActivityEvent.setValue(
            getAuthUseCase.isAuthenticated() ?
                ActivityToStart.HOME_ACTIVITY :
                ActivityToStart.AUTH_ACTIVITY
        );

        return startActivityEvent;
    }

    // ------------------------------------------- ENUM --------------------------------------------

    enum ActivityToStart {
        HOME_ACTIVITY,
        AUTH_ACTIVITY,
    }
}
