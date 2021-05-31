package com.neige_i.go4lunch.view.auth;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.firebase.model.User;
import com.neige_i.go4lunch.domain.firestore.CreateFirestoreUserUseCase;
import com.neige_i.go4lunch.domain.firestore.GetFirestoreUserUseCase;
import com.neige_i.go4lunch.view.util.MediatorSingleLiveEvent;
import com.neige_i.go4lunch.view.util.SingleLiveEvent;

import static com.neige_i.go4lunch.view.auth.AuthActivity.GOOGLE_SIGN_IN_REQUEST_CODE;

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
    private final MediatorSingleLiveEvent<Void> startHomeActivityEvent = new MediatorSingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<LoginManager> facebookLoginEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Integer> showErrorEvent = new SingleLiveEvent<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    @NonNull
    private final CallbackManager callbackManager;
    @NonNull
    private final LoginManager loginManager;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public AuthViewModel(@NonNull FirebaseAuth firebaseAuth,
                         @NonNull GetFirestoreUserUseCase getFirestoreUserUseCase,
                         @NonNull CreateFirestoreUserUseCase createFirestoreUserUseCase
    ) {
        this.firebaseAuth = firebaseAuth;
        this.getFirestoreUserUseCase = getFirestoreUserUseCase;
        this.createFirestoreUserUseCase = createFirestoreUserUseCase;

        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                firebaseAuthWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken()));
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                if ("net::ERR_INTERNET_DISCONNECTED".equals(error.getMessage())) {
                    showErrorEvent.setValue(R.string.no_internet_error);
                } else {
                    showErrorEvent.setValue(R.string.default_connection_error);
                }
            }
        });
    }

    @NonNull
    public LiveData<Void> getStartHomeActivityEvent() {
        return startHomeActivityEvent;
    }

    @NonNull
    public LiveData<LoginManager> getFacebookLoginEvent() {
        return facebookLoginEvent;
    }

    @NonNull
    public LiveData<Integer> getShowErrorEvent() {
        return showErrorEvent;
    }

    // -------------------------------------- SIGN-IN METHODS --------------------------------------

    public void onFacebookLoggedIn() {
        facebookLoginEvent.setValue(loginManager);
    }

    public void onSignInResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            try {
                final String idToken = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException.class)
                    .getIdToken();
                firebaseAuthWithCredential(GoogleAuthProvider.getCredential(idToken, null));
            } catch (ApiException ignored) {
            }
        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithCredential(@NonNull AuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener(authResult -> {
                final FirebaseUser firebaseUser = authResult.getUser();
                assert firebaseUser != null; // Just successfully signed in
                final String userId = firebaseUser.getUid();

                startHomeActivityEvent.addSource(getFirestoreUserUseCase.userAlreadyExists(userId), doesExist -> {
                    if (!doesExist) {

                        // getDisplayName() and getPhotoUrl() return null when sign in with Google
                        String userName = null;
                        String profileImageUrl = null;
                        for (UserInfo userInfo : firebaseUser.getProviderData()) {
                            if (userInfo.getDisplayName() != null) {
                                userName = userInfo.getDisplayName();
                                profileImageUrl = userInfo.getPhotoUrl().toString();
                                break;
                            }
                        }

                        createFirestoreUserUseCase.createUser(
                            userId,
                            new User(
                                userId,
                                firebaseUser.getEmail(),
                                userName,
                                profileImageUrl,
                                null
                            )
                        );
                    }
                });

                startHomeActivityEvent.call();
            })
            .addOnFailureListener(e -> {
                if (e instanceof FirebaseNetworkException) {
                    showErrorEvent.setValue(R.string.no_internet_error);
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    showErrorEvent.setValue(R.string.invalid_user_error);
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showErrorEvent.setValue(R.string.invalid_credentials_error);
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    showErrorEvent.setValue(R.string.user_collision_error);
                } else {
                    showErrorEvent.setValue(R.string.general_error);
                }
            });
    }
}
