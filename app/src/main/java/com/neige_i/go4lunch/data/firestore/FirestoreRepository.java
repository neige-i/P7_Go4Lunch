package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface FirestoreRepository {

    @NonNull
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @NonNull
    LiveData<User> getUser(@NonNull String userId);

    void addUser(@NonNull String userId, @NonNull User user);

    @NonNull
    LiveData<List<User>> getWorkmatesEatingAt(@NonNull String restaurantId);

    @NonNull
    LiveData<List<User>> getAllUsers();

    void removeListenerRegistrations();
}
