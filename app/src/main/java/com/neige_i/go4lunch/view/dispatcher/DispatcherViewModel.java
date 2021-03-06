package com.neige_i.go4lunch.view.dispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.domain.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.view.auth.AuthActivity;
import com.neige_i.go4lunch.view.home.HomeActivity;
import com.neige_i.go4lunch.view.util.SingleLiveEvent;

public class DispatcherViewModel extends ViewModel {

    @NonNull
    private final GetFirebaseUserUseCase getFirebaseUserUseCase;

    @NonNull
    private final SingleLiveEvent<Class<? extends AppCompatActivity>> startActivityEvent = new SingleLiveEvent<>();

    public DispatcherViewModel(@NonNull GetFirebaseUserUseCase getFirebaseUserUseCase) {
        this.getFirebaseUserUseCase = getFirebaseUserUseCase;
    }

    @NonNull
    public LiveData<Class<? extends AppCompatActivity>> getStartActivityEvent() {
        return startActivityEvent;
    }

    public void onSignedInUserChecked() {
        startActivityEvent.setValue(getFirebaseUserUseCase.getFirebaseUser() != null ?
                                        HomeActivity.class :
                                        AuthActivity.class);
    }
}
