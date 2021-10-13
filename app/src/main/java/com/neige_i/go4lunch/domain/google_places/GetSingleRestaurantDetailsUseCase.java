package com.neige_i.go4lunch.domain.google_places;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.domain.model.DetailsModel;

public interface GetSingleRestaurantDetailsUseCase {

    @NonNull
    LiveData<DetailsModel> getDetailsItem(@NonNull String placeId);
}
