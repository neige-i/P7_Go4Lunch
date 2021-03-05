package com.neige_i.go4lunch.view.dispatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.view.auth.AuthActivity;
import com.neige_i.go4lunch.view.home.HomeActivity;
import com.neige_i.go4lunch.view.util.SingleLiveEvent;

public class DispatcherViewModel extends ViewModel {

    private final SingleLiveEvent<Class<? extends AppCompatActivity>> startActivityEvent = new SingleLiveEvent<>();

    public LiveData<Class<? extends AppCompatActivity>> getStartActivityEvent() {
        return startActivityEvent;
    }

    public void onSignedInUserChecked() {
        startActivityEvent.setValue(FirebaseAuth.getInstance().getCurrentUser() != null ?
                                        HomeActivity.class :
                                        AuthActivity.class);
    }
}
