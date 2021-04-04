package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firebase.FirebaseRepository;

public class GetFirebaseUserOldUseCaseImpl implements GetFirebaseUserOldUseCase {

    @NonNull
    private final FirebaseRepository firebaseRepository;

    public GetFirebaseUserOldUseCaseImpl(@NonNull FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Nullable
    @Override
    public FirebaseUser getFirebaseUser() {
        return firebaseRepository.getCurrentUser();
    }
}
