package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetDrawerInfoUseCase {

    @NonNull
    LiveData<DrawerInfo> get();
}
