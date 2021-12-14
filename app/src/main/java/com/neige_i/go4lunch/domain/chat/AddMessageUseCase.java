package com.neige_i.go4lunch.domain.chat;

import androidx.annotation.NonNull;

public interface AddMessageUseCase {

    void add(@NonNull String workmateId, @NonNull String message);
}
