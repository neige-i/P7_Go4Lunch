package com.neige_i.go4lunch.domain.to_sort;

import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

public interface GetFirestoreUserListUseCase {

    LiveData<List<User>> getAllUsers();
}
