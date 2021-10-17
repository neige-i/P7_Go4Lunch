package com.neige_i.go4lunch.domain.dispatcher;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class GetAuthUseCaseImpl implements GetAuthUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final FirebaseAuth firebaseAuth;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    GetAuthUseCaseImpl(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public boolean isAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
