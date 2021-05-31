package com.neige_i.go4lunch.domain.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.api.ResolvableApiException;

public interface GetGpsResolvableUseCase {

    @NonNull
    LiveData<ResolvableApiException> getResolvable();
}
