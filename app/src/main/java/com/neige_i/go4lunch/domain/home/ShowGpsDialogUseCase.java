package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.api.ResolvableApiException;

public interface ShowGpsDialogUseCase {

    @NonNull
    LiveData<ResolvableApiException> getDialog();
}
