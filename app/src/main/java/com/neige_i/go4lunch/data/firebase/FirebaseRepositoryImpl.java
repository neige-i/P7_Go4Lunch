package com.neige_i.go4lunch.data.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepositoryImpl implements FirebaseRepository {

    @NonNull
    private final MutableLiveData<String> selectedRestaurant = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<List<String>> favoriteRestaurants = new MutableLiveData<>();

    public FirebaseRepositoryImpl() {
        favoriteRestaurants.setValue(new ArrayList<>());
    }

    @Nullable
    @Override
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser(); // ASKME: wrap return type inside LiveData
    }

    @NonNull
    @Override
    public LiveData<String> getSelectedRestaurant() {
        return selectedRestaurant;
    }

    @Override
    public void setSelectedRestaurant(@NonNull String placeId) {
        selectedRestaurant.setValue(placeId);
    }

    @Override
    public void clearSelectedRestaurant() {
        selectedRestaurant.setValue(null);
    }

    @NonNull
    @Override
    public LiveData<List<String>> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    @Override
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