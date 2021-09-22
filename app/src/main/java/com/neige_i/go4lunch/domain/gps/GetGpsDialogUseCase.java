package com.neige_i.go4lunch.domain.gps;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.api.ResolvableApiException;

public interface GetGpsDialogUseCase {

    @NonNull
    LiveData<ResolvableApiException> showDialog();
}
