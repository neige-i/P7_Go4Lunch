package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetGpsStatusUseCase {

    @NonNull
    LiveData<Boolean> isEnabled();
}
