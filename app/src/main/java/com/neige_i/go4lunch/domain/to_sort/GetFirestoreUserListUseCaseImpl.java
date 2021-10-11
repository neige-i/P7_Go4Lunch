package com.neige_i.go4lunch.domain.to_sort;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.FirestoreRepository;
import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

import javax.inject.Inject;

public class GetFirestoreUserListUseCaseImpl implements GetFirestoreUserListUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;

    @Inject
    public GetFirestoreUserListUseCaseImpl(@NonNull FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public LiveData<List<User>> getAllUsers() {
        return firestoreRepository.getAllUsers();
    }
}
