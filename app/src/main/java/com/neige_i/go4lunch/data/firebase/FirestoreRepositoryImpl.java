package com.neige_i.go4lunch.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

public class FirestoreRepositoryImpl implements FirestoreRepository {

    @NonNull
    public static final String USER_COLLECTION = "users";
    @NonNull
    public static final String RESTAURANT_COLLECTION = "restaurants";

    @NonNull
    private final FirebaseFirestore firebaseFirestore;

    public FirestoreRepositoryImpl(@NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    @NonNull
    @Override
    public LiveData<User> getUser(@NonNull String userId) {
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> userMutableLiveData.setValue(documentSnapshot.toObject(User.class)));

        return userMutableLiveData;
    }

    @Override
    public void addUser(@NonNull String userId, @NonNull User userToAdd) {
        firebaseFirestore.collection(USER_COLLECTION).document(userId).set(userToAdd);
    }

    @NonNull
    @Override
    public LiveData<List<User>> getAllUsers() {
        final MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();

        firebaseFirestore.collection(USER_COLLECTION).addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                return;
            }

            if (querySnapshot != null) {
                userListMutableLiveData.setValue(querySnapshot.toObjects(User.class));
            }
        });

        return userListMutableLiveData;
    }

    @Override
    public void setSelectedRestaurant(@NonNull String userId, @NonNull User.SelectedRestaurant selectedRestaurant) {
        firebaseFirestore.collection(USER_COLLECTION).document(userId).update("selectedRestaurant", selectedRestaurant);
    }

    @Override
    public void clearSelectedRestaurant(@NonNull String userId) {
        firebaseFirestore.collection(USER_COLLECTION).document(userId).update("selectedRestaurant", null);
    }

    @NonNull
    @Override
    public LiveData<List<Restaurant>> getAllRestaurants() {
        final MutableLiveData<List<Restaurant>> restaurantListMutableLiveData = new MutableLiveData<>();

        firebaseFirestore.collection(RESTAURANT_COLLECTION).addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                // Display error to user
                return;
            }

            if (querySnapshot != null) {
                restaurantListMutableLiveData.setValue(querySnapshot.toObjects(Restaurant.class));
            }
        });

        return restaurantListMutableLiveData;
    }

    @NonNull
    @Override
    public LiveData<Restaurant> getRestaurant(@NonNull String restaurantId) {
        final MutableLiveData<Restaurant> restaurantMutableLiveData = new MutableLiveData<>();

        firebaseFirestore.collection(RESTAURANT_COLLECTION)
            .document(restaurantId)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    // Display error to user
                    return;
                }

                if (value != null) {
                    restaurantMutableLiveData.setValue(value.toObject(Restaurant.class));
                }
            });

        return restaurantMutableLiveData;
    }

    @Override
    public void addInterestedWorkmate(@NonNull String restaurantId, @NonNull String workmateId) {
        firebaseFirestore.collection(RESTAURANT_COLLECTION).document(restaurantId)
//            .set(new Restaurant(restaurantId, workmateId))
//            .addOnCompleteListener(task -> Log.d("Neige", "FirestoreRepositoryImpl::onComplete: " + task.isSuccessful()));
            .update("interestedWorkmates", FieldValue.arrayUnion(workmateId))
            .addOnCompleteListener(task -> Log.d("Neige", "FirestoreRepositoryImpl::onComplete update: " + task.isSuccessful()));
    }
}
