package com.neige_i.go4lunch.data.firestore;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.LocalDate;
import java.util.List;

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
    @Nullable
    ListenerRegistration getInterestedWorkmatesListener;
    @Nullable
    ListenerRegistration getAllWorkmatesListener;

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
                    final User user = documentSnapshot.toObject(User.class);
                    Log.d("Neige", "REPO getUser with ID='" + userId + "': " + user);
                    userMutableLiveData.setValue(user);
                }
            });

        return userMutableLiveData;
    }

    @Override
    public void addUser(@NonNull String userId, @NonNull User user) {
        Log.d("Neige", "REPO addFirestoreUser with ID='" + userId + "': " + user);
        firebaseFirestore.collection(USER_COLLECTION).document(userId).set(user);
    }

    @NonNull
    @Override
    public LiveData<List<User>> getWorkmatesEatingAt(@NonNull String restaurantId) {
        final MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();

        getInterestedWorkmatesListener = firebaseFirestore.collection(USER_COLLECTION)
            .whereEqualTo("selectedRestaurantId", restaurantId)
            .whereEqualTo("selectedRestaurantDate", LocalDate.now().format(DATE_FORMATTER))
            .addSnapshotListener((querySnapshot, error) -> {
                if (querySnapshot != null) {
                    final List<User> userList = querySnapshot.toObjects(User.class);
                    Log.d("Neige", "REPO get workmates eating TODAY at ID='" + restaurantId + "': " + userList.size());
                    usersMutableLiveData.setValue(userList);
                }
            });

        return usersMutableLiveData;
    }

    @NonNull
    @Override
    public LiveData<List<User>> getAllUsers() {
        final MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();

        getAllWorkmatesListener = firebaseFirestore.collection(USER_COLLECTION)
            .addSnapshotListener((querySnapshot, error) -> {
                if (querySnapshot != null) {
                    final List<User> userList = querySnapshot.toObjects(User.class);
                    Log.d("Neige", "REPO get all workmates: " + userList.size());
                    usersMutableLiveData.setValue(userList);
                }
            });

        return usersMutableLiveData;
    }

    @Override
    public void removeListenerRegistrations() {
        if (getUserListener != null) {
            getUserListener.remove();
        }
        if (getInterestedWorkmatesListener != null) {
            getInterestedWorkmatesListener.remove();
        }
        if (getAllWorkmatesListener != null) {
            getAllWorkmatesListener.remove();
        }
    }
}
