package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.User;

public class CreateFirestoreUserUseCaseImpl implements CreateFirestoreUserUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;

    public CreateFirestoreUserUseCaseImpl(@NonNull FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public void createUser(@NonNull String userId, @NonNull User userToAdd) {
        firestoreRepository.addUser(userId, userToAdd);
    }
}
