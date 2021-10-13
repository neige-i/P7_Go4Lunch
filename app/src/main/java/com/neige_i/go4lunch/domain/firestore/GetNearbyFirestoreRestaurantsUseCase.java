package com.neige_i.go4lunch.domain.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.neige_i.go4lunch.data.firebase.model.Restaurant;

import java.util.List;

public interface GetNearbyFirestoreRestaurantsUseCase {

    LiveData<List<Restaurant>> get();
}
