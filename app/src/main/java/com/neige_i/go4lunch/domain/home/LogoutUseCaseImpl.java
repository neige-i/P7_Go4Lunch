package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class LogoutUseCaseImpl implements LogoutUseCase {

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    LogoutUseCaseImpl(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }
}
