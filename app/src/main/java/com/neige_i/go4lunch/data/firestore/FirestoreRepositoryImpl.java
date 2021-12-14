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
    public LiveData<ChatRoom> getChatRoom(@NonNull String roomId) {
        final MutableLiveData<ChatRoom> chatRoomMutableLiveData = new MutableLiveData<>();

        getChatRoomListener = firebaseFirestore.collection(MESSAGE_COLLECTION)
            .document(roomId)
            .addSnapshotListener((documentSnapshot, error) -> {
                if (documentSnapshot != null) {
                    chatRoomMutableLiveData.setValue(documentSnapshot.toObject(ChatRoom.class));
                }
            });

        return chatRoomMutableLiveData;
    }

    @Override
    public void chatRoomExist(@NonNull String roomId, @NonNull OnChatRoomResult onChatRoomResult) {
        firebaseFirestore.collection(MESSAGE_COLLECTION)
            .document(roomId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                onChatRoomResult.onResult(documentSnapshot.exists());
            });
    }

    @Override
    public void addChatRoom(@NonNull String roomId, @NonNull ChatRoom chatRoom) {
        firebaseFirestore.collection(MESSAGE_COLLECTION)
            .document(roomId)
            .set(chatRoom);
    }

    @Override
    public void addMessageToChat(@NonNull String roomId, @NonNull ChatRoom.Message message) {
        firebaseFirestore.collection(MESSAGE_COLLECTION)
            .document(roomId)
            .update("messages", FieldValue.arrayUnion(message));
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
