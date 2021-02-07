package com.neige_i.go4lunch.data.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepository {

    @NonNull
    private final MutableLiveData<String> selectedRestaurant = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<List<String>> favoriteRestaurants = new MutableLiveData<>();

    public FirebaseRepository() {
        favoriteRestaurants.setValue(new ArrayList<>());
    }

    @NonNull
    public LiveData<String> getSelectedRestaurant() {
        return selectedRestaurant;
    }

    @NonNull
    public LiveData<List<String>> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setSelectedRestaurant(@NonNull String placeId) {
        selectedRestaurant.setValue(placeId);
    }

    public void clearSelectedRestaurant() {
        selectedRestaurant.setValue(null);
    }

    public void toggleFavoriteRestaurant(@NonNull String placeId) {
        final List<String> currentFavoriteRestaurants = favoriteRestaurants.getValue();

        if (currentFavoriteRestaurants != null) {
            if (currentFavoriteRestaurants.contains(placeId))
                currentFavoriteRestaurants.remove(placeId);
            else
                currentFavoriteRestaurants.add(placeId);

            favoriteRestaurants.setValue(currentFavoriteRestaurants);
        }
    }
}
