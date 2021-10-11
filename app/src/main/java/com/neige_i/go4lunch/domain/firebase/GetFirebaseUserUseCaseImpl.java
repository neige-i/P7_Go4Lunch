package com.neige_i.go4lunch.domain.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class GetFirebaseUserUseCaseImpl implements GetFirebaseUserUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final FirebaseAuth firebaseAuth;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public GetFirebaseUserUseCaseImpl(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Nullable
    @Override
    public FirebaseUser getUser() {
        return firebaseAuth.getCurrentUser();
    }
}
