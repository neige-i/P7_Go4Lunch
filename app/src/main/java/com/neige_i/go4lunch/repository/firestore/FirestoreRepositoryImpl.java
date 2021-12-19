package com.neige_i.go4lunch.repository.firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirestoreRepositoryImpl implements FirestoreRepository {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    @NonNull
    static final String USER_COLLECTION = "users";
    @NonNull
    static final String MESSAGE_COLLECTION = "messages";

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
    @Nullable
    private ListenerRegistration getChatRoomListener;

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
                    userMutableLiveData.setValue(documentSnapshot.toObject(User.class));
                }
            });

        return userMutableLiveData;
    }

    @Nullable
    @Override
    public User getUserByIdSync(@NonNull String userId) {
        try {
            final DocumentSnapshot documentSnapshot = Tasks.await(
                firebaseFirestore
                    .collection(USER_COLLECTION)
                    .document(userId)
                    .get()
            );

            if (documentSnapshot == null) {
                return null;
            }

            return documentSnapshot.toObject(User.class);

        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
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
                    usersMutableLiveData.setValue(querySnapshot.toObjects(User.class));
                }
            });

        return usersMutableLiveData;
    }

    @NonNull
    @Override
    public List<User> getWorkmatesEatingAtSync(@NonNull String restaurantId) {
        try {
            final QuerySnapshot querySnapshot = Tasks.await(
                firebaseFirestore
                    .collection(USER_COLLECTION)
                    .whereEqualTo("selectedRestaurant.id", restaurantId)
                    .whereEqualTo("selectedRestaurant.date", LocalDate.now(clock).format(DATE_FORMATTER))
                    .get()
            );

            if (querySnapshot == null) {
                return new ArrayList<>();
            } else {
                return querySnapshot.toObjects(User.class);
            }
        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public LiveData<List<User>> getAllUsers() {
        final MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();

        getAllWorkmatesListener = firebaseFirestore.collection(USER_COLLECTION)
            .addSnapshotListener((querySnapshot, error) -> {
                if (querySnapshot != null) {
                    usersMutableLiveData.setValue(querySnapshot.toObjects(User.class));
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
        @NonNull String restaurantName,
        @NonNull String restaurantAddress
    ) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .update(
                "selectedRestaurant.id", placeId,
                "selectedRestaurant.date", LocalDate.now(clock).format(DATE_FORMATTER),
                "selectedRestaurant.name", restaurantName,
                "selectedRestaurant.address", restaurantAddress
            );
    }

    @Override
    public void clearSelectedRestaurant(@NonNull String userId) {
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .update("selectedRestaurant", null);
    }

    @NonNull
    @Override
    public String getRoomId(@NonNull String userId1, @NonNull String userId2) {
        // Create a unique room ID from the users' ID. The IDs are sorted in ascending order
        // to always get the same room ID whoever creates the room
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + userId2;
        } else {
            return userId2 + userId1;
        }
    }

    @NonNull
    @Override
    public LiveData<List<Message>> getMessagesByRoomId(@NonNull String roomId) {
        final MutableLiveData<List<Message>> messagesMutableLiveData = new MutableLiveData<>();

        getChatRoomListener = firebaseFirestore
            .collection(MESSAGE_COLLECTION)
            .whereEqualTo("roomId", roomId)
            .addSnapshotListener((querySnapshot, error) -> {
                if (querySnapshot != null) {
                    messagesMutableLiveData.setValue(querySnapshot.toObjects(Message.class));
                }
            });

        return messagesMutableLiveData;
    }

    @Override
    public void addMessage(@NonNull Message messageToAdd) {
        firebaseFirestore
            .collection(MESSAGE_COLLECTION)
            .add(messageToAdd);
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
        if (getChatRoomListener != null) {
            getChatRoomListener.remove();
        }
    }
}
