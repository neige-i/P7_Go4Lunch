package com.neige_i.go4lunch.domain.firestore;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.model.User;

public interface CreateFirestoreUserUseCase {

    void createUser(@NonNull String userId, @NonNull User userToAdd);
}
