package com.neige_i.go4lunch.domain.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetChatInfoUseCase {

    @NonNull
    LiveData<ChatInfo> get(@NonNull String workmateId);
}
