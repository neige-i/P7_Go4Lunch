package com.neige_i.go4lunch.view.settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.domain.settings.HandleSettingsPreferencesUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class SettingsViewModel extends ViewModel {

    @NonNull
    private final HandleSettingsPreferencesUseCase handleSettingsPreferencesUseCase;

    @NonNull
    private final MutableLiveData<Boolean> viewState = new MutableLiveData<>();
    @NonNull
    private final SingleLiveEvent<Void> closeActivityEvent = new SingleLiveEvent<>();

    @Inject
    SettingsViewModel(@NonNull HandleSettingsPreferencesUseCase handleSettingsPreferencesUseCase) {
        this.handleSettingsPreferencesUseCase = handleSettingsPreferencesUseCase;

        viewState.setValue(handleSettingsPreferencesUseCase.getMiddayNotification());
    }

    @NonNull
    LiveData<Boolean> getViewState() {
        return viewState;
    }

    @NonNull
    public LiveData<Void> getCloseActivityEvent() {
        return closeActivityEvent;
    }

    void onNotificationSwitchCheckChanged(boolean isChecked) {
        handleSettingsPreferencesUseCase.setMiddayNotification(isChecked);
    }

    public void onOptionsItemSelected(int itemId) {
        if (itemId == android.R.id.home) {
            closeActivityEvent.call();
        }
    }
}
