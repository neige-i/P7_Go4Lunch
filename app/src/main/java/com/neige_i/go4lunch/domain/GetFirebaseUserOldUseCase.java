package com.neige_i.go4lunch.domain;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;

public interface GetFirebaseUserOldUseCase {

    @Nullable
    FirebaseUser getFirebaseUser();
}
