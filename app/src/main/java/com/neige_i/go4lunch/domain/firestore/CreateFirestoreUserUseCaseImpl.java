package com.neige_i.go4lunch.domain.firestore;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.User;

public class CreateFirestoreUserUseCaseImpl implements CreateFirestoreUserUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final FirestoreRepository firestoreRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public CreateFirestoreUserUseCaseImpl(@NonNull FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @Override
    public void createUser(@NonNull String userId, @NonNull User userToAdd) {
        firestoreRepository.addUser(userId, userToAdd);
    }
}
