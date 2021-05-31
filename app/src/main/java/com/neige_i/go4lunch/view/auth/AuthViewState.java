package com.neige_i.go4lunch.view.auth;

import java.util.Objects;

class AuthViewState {

    private final boolean progressBarVisible;
    private final boolean buttonEnabled;

    AuthViewState(boolean progressBarVisible, boolean buttonEnabled) {
        this.progressBarVisible = progressBarVisible;
        this.buttonEnabled = buttonEnabled;
    }

    public boolean isProgressBarVisible() {
        return progressBarVisible;
    }

    public boolean isButtonEnabled() {
        return buttonEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthViewState that = (AuthViewState) o;
        return progressBarVisible == that.progressBarVisible &&
            buttonEnabled == that.buttonEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(progressBarVisible, buttonEnabled);
    }
}
