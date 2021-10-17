package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirestoreRepositoryImpl implements FirestoreRepository {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    @NonNull
    private static final String USER_COLLECTION = "users";

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final FirebaseFirestore firebaseFirestore;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @Nullable
    ListenerRegistration getUserListener;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public FirestoreRepositoryImpl(@NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    public LiveData<User> getUser(@NonNull String userId) {
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

        getUserListener = firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .addSnapshotListener((documentSnapshot, error) -> {
                if (documentSnapshot != null) {
                    userMutableLiveData.setValue(documentSnapshot.toObject(User.class));
                }
            });

        return userMutableLiveData;
    }

    @Override
    public void addUser(@NonNull String userId, @NonNull User user) {
        firebaseFirestore.collection(USER_COLLECTION).document(userId).set(user);
    }

    @Override
    public void removeListenerRegistrations() {
        if (getUserListener != null) {
            getUserListener.remove();
        }
    }
}
