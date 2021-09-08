package com.neige_i.go4lunch.domain.to_sort;

import androidx.annotation.NonNull;

import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

public interface UpdateInterestedWorkmatesUseCase {

    void addWorkmateToList(@NonNull String restaurantId, @NonNull Restaurant.InterestedWorkmate interestedWorkmate);

    void removeWorkmateToList(@NonNull String restaurantId, @NonNull Restaurant.InterestedWorkmate interestedWorkmate);
}
