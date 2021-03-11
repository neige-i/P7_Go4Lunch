package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;

import java.util.Objects;

public class GetFirestoreUserUseCaseImpl implements GetFirestoreUserUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;

    public GetFirestoreUserUseCaseImpl(@NonNull FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public LiveData<Boolean> userAlreadyExists(@NonNull String uid) {
        return Transformations.map(firestoreRepository.getUser(uid), Objects::nonNull);
    }
}
