package com.neige_i.go4lunch.domain;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;

public interface GetFirebaseUserUseCase {

    @Nullable
    FirebaseUser getFirebaseUser();
}
