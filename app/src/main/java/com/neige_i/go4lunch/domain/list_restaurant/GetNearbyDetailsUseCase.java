package com.neige_i.go4lunch.domain.list_restaurant;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

public interface GetNearbyDetailsUseCase {

    @NonNull
    LiveData<List<NearbyDetail>> get();
}
