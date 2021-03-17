package com.neige_i.go4lunch.data.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.neige_i.go4lunch.data.firebase.model.User;

import java.util.List;

public class FirestoreRepositoryImpl implements FirestoreRepository {

    @NonNull
    public static final String USER_COLLECTION = "users";

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
}
