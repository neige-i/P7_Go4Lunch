package com.neige_i.go4lunch.domain.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetMapDataUseCase {

    @NonNull
    LiveData<MapData> get();

    void refresh();
}
