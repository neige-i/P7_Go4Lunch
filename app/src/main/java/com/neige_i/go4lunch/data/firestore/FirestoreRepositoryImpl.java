package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirestoreRepositoryImpl implements FirestoreRepository {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    @NonNull
    static final String USER_COLLECTION = "users";

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    @NonNull
    private final Clock clock;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @Nullable
    private ListenerRegistration getUserListener;
    @Nullable
    private ListenerRegistration getInterestedWorkmatesListener;
    @Nullable
    private ListenerRegistration getAllWorkmatesListener;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    public FirestoreRepositoryImpl(
        @NonNull FirebaseFirestore firebaseFirestore,
        @NonNull Clock clock
    ) {
        this.firebaseFirestore = firebaseFirestore;
        this.clock = clock;
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
                    userMutableLiveData.setValue(user);
                }
            });

        return userMutableLiveData;
    }

    @Override
    public void addUser(@NonNull String userId, @NonNull User user) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .set(user);
    }

    @NonNull
    @Override
    public LiveData<List<User>> getWorkmatesEatingAt(@NonNull String restaurantId) {
        final MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();

        getInterestedWorkmatesListener = firebaseFirestore.collection(USER_COLLECTION)
            .whereEqualTo("selectedRestaurant.id", restaurantId)
            .whereEqualTo("selectedRestaurant.date", LocalDate.now(clock).format(DATE_FORMATTER))
            .addSnapshotListener((querySnapshot, error) -> {
                if (querySnapshot != null) {
                    final List<User> userList = querySnapshot.toObjects(User.class);
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
                    usersMutableLiveData.setValue(userList);
                }
            });

        return usersMutableLiveData;
    }

    @Override
    public void addToFavoriteRestaurant(@NonNull String userId, @NonNull String placeId) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .update("favoriteRestaurants", FieldValue.arrayUnion(placeId));
    }

    @Override
    public void removeFromFavoriteRestaurant(@NonNull String userId, @NonNull String placeId) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .update("favoriteRestaurants", FieldValue.arrayRemove(placeId));
    }

    @Override
    public void setSelectedRestaurant(
        @NonNull String userId,
        @NonNull String placeId,
        @NonNull String restaurantName
    ) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .update(
                "selectedRestaurant.id", placeId,
                "selectedRestaurant.date", LocalDate.now(clock).format(DATE_FORMATTER),
                "selectedRestaurant.name", restaurantName
            );
    }

    @Override
    public void clearSelectedRestaurant(@NonNull String userId) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .update("selectedRestaurant", null);
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
