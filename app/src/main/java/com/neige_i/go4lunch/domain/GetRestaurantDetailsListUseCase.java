package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.domain.model.ListModel;

public interface GetRestaurantDetailsListUseCase {

    @NonNull
    LiveData<ListModel> getDetailsList();
}
