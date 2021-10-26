package com.neige_i.go4lunch.domain.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface GetRestaurantInfoUseCase {

    @NonNull
    LiveData<RestaurantInfo> get(@NonNull String placeId);
}
