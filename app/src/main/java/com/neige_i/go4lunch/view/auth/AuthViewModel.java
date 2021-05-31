package com.neige_i.go4lunch.view.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.FacebookException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.firebase.model.User;
import com.neige_i.go4lunch.domain.firestore.CreateFirestoreUserUseCase;
import com.neige_i.go4lunch.domain.firestore.GetFirestoreUserUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;
import com.neige_i.go4lunch.view.SingleLiveEvent;

// TODO: write tests
public class AuthViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final GetFirestoreUserUseCase getFirestoreUserUseCase;
    @NonNull
    private final CreateFirestoreUserUseCase createFirestoreUserUseCase;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MutableLiveData<AuthViewState> authViewState = new MutableLiveData<>();
    @NonNull
    private final MediatorSingleLiveEvent<Void> startHomeActivityEvent = new MediatorSingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Integer> showErrorEvent = new SingleLiveEvent<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public AuthViewModel(@NonNull FirebaseAuth firebaseAuth,
                         @NonNull GetFirestoreUserUseCase getFirestoreUserUseCase,
                         @NonNull CreateFirestoreUserUseCase createFirestoreUserUseCase
    ) {
        this.firebaseAuth = firebaseAuth;
        this.getFirestoreUserUseCase = getFirestoreUserUseCase;
        this.createFirestoreUserUseCase = createFirestoreUserUseCase;
    }

    @NonNull
    public LiveData<AuthViewState> getAuthViewState() {
        return authViewState;
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

    public void signInToFirebase(@NonNull AuthCredential credential) {
        // The sign-in process may take a few seconds,
        // so it is convenient to show a ProgressBar and disable sign-in buttons
        // to let the user know that some business is running in the background
        authViewState.setValue(new AuthViewState(true, false));

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener(authResult -> {
                assert authResult.getUser() != null; // Just successfully signed in
                addUserToFirestore(authResult.getUser());

                startHomeActivityEvent.call();
            })
            .addOnFailureListener(e -> {
                authViewState.setValue(new AuthViewState(false, true)); // Reset view state
                handleFirebaseSignInError(e);
            });
    }

    private void addUserToFirestore(@NonNull FirebaseUser firebaseUser) {
        final String userId = firebaseUser.getUid();

        // To get the LiveData's value, the LiveData must be observed
        startHomeActivityEvent.addSource(getFirestoreUserUseCase.userAlreadyExists(userId), doesExist -> {
            if (!doesExist) {
                createFirestoreUserUseCase.createUser(
                    userId,
                    new User(
                        firebaseUser.getEmail(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                        null
                    )
                );
            }
        });
    }

    // ----------------------------------- SIGN-IN ERROR METHODS -----------------------------------

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
}
