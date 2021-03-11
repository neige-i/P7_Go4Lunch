package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.User;

public interface CreateFirestoreUserUseCase {

    void createUser(@NonNull String userId, @NonNull User userToAdd);
}
