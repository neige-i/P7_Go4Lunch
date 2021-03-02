package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.domain.model.DetailsModel;

public interface GetRestaurantDetailsItemUseCase {

    @NonNull
    LiveData<DetailsModel> getDetailsItem(@NonNull String placeId);
}
