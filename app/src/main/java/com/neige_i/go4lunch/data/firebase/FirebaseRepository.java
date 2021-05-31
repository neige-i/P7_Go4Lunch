package com.neige_i.go4lunch.data.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public interface FirebaseRepository {

    @NonNull
    LiveData<String> getSelectedRestaurant();

    void setSelectedRestaurant(@NonNull String placeId);

    void clearSelectedRestaurant();

    @NonNull
    LiveData<List<String>> getFavoriteRestaurants();

    void toggleFavoriteRestaurant(@NonNull String placeId);
}
