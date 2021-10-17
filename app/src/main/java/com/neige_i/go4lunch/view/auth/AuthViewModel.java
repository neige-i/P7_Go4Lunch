package com.neige_i.go4lunch.view.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.FacebookException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCase;
import com.neige_i.go4lunch.domain.auth.SignInResult;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final SignInAndUpdateDatabaseUseCase signInAndUpdateDatabaseUseCase;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<Boolean> loggingViewState = new MediatorLiveData<>();
    @NonNull
    private final SingleLiveEvent<Void> startHomeActivityEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Integer> showErrorEvent = new SingleLiveEvent<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public AuthViewModel(@NonNull SignInAndUpdateDatabaseUseCase signInAndUpdateDatabaseUseCase) {
        this.signInAndUpdateDatabaseUseCase = signInAndUpdateDatabaseUseCase;
    }

    @NonNull
    public LiveData<Boolean> getLoggingViewState() {
        return loggingViewState;
    }

    @NonNull
    public LiveData<Void> getStartHomeActivityEvent() {
        return startHomeActivityEvent;
    }

    @NonNull
    public LiveData<Integer> getShowErrorEvent() {
        return showErrorEvent;
    }

    // -------------------------------------- SIGN-IN METHODS --------------------------------------

    public void signInToFirebase(@NonNull AuthCredential authCredential) {
        // Logging process has begun
        loggingViewState.setValue(true);

        loggingViewState.addSource(signInAndUpdateDatabaseUseCase.signInToFirebase(authCredential), signInResult -> {
            if (signInResult instanceof SignInResult.Success) {
                startHomeActivityEvent.call();
            } else if (signInResult instanceof SignInResult.Failure) {
                // The activity is not directly started when the sign-in succeeds (a little laggy)
                // This is why the logging is reset only when the the sign-in fails
                loggingViewState.setValue(false);

                handleFirebaseSignInError(((SignInResult.Failure) signInResult).getException());
            }
        });
    }

    // ----------------------------------- SIGN-IN ERROR METHODS -----------------------------------

    private void handleFirebaseSignInError(@NonNull Exception e) {
        final int errorMessageId;

        if (e instanceof FirebaseNetworkException) {
            errorMessageId = R.string.no_internet_error;
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            errorMessageId = R.string.invalid_user_error;
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessageId = R.string.invalid_credentials_error;
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            errorMessageId = R.string.user_collision_error;
        } else {
            errorMessageId = R.string.default_login_error;
        }

        showErrorEvent.setValue(errorMessageId);
    }

    public void handleGoogleSignInError(@NonNull ApiException e) {
        final int errorMessageId;

        switch (e.getStatusCode()) {
            case CommonStatusCodes.NETWORK_ERROR:
                errorMessageId = R.string.no_internet_error;
                break;
            case CommonStatusCodes.INVALID_ACCOUNT:
                errorMessageId = R.string.invalid_account_error;
                break;
            case CommonStatusCodes.TIMEOUT:
                errorMessageId = R.string.timeout_error;
                break;
            default:
                errorMessageId = R.string.default_login_error;
        }

        showErrorEvent.setValue(errorMessageId);
    }

    public void handleFacebookSignInError(@NonNull FacebookException error) {
        final int errorMessageId;

        if ("net::ERR_INTERNET_DISCONNECTED".equals(error.getMessage())) {
            errorMessageId = R.string.no_internet_error;
        } else {
            errorMessageId = R.string.default_login_error;
        }

        showErrorEvent.setValue(errorMessageId);
    }
}
