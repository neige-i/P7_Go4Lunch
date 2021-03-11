package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetFirestoreUserUseCase {

    LiveData<Boolean> userAlreadyExists(@NonNull String uid);
}
