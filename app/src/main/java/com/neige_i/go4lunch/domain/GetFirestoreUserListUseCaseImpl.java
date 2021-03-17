package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

public class GetFirestoreUserListUseCaseImpl implements GetFirestoreUserListUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;

    public GetFirestoreUserListUseCaseImpl(@NonNull FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public LiveData<List<User>> getAllUsers() {
        return firestoreRepository.getAllUsers();
    }
}
