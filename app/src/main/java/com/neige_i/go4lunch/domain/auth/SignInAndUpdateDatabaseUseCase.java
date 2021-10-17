package com.neige_i.go4lunch.domain.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;

public interface SignInAndUpdateDatabaseUseCase {

    @NonNull
    LiveData<SignInResult> signInToFirebase(@NonNull AuthCredential authCredential);
}
