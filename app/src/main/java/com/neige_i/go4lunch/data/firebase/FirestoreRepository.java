package com.neige_i.go4lunch.data.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

public interface FirestoreRepository {

    @NonNull
    LiveData<User> getUser(@NonNull String userId);

    void addUser(@NonNull String userId, @NonNull User userToAdd);

    @NonNull
    LiveData<List<User>> getAllUsers();
}
