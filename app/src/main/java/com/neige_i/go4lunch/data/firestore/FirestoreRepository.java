package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface FirestoreRepository {

    @NonNull
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @NonNull
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @NonNull
    LiveData<User> getUser(@NonNull String userId);

    void addUser(@NonNull String userId, @NonNull User user);

    @NonNull
    LiveData<List<User>> getWorkmatesEatingAt(@NonNull String restaurantId);

    @NonNull
    LiveData<List<User>> getAllUsers();

    void addToFavoriteRestaurant(@NonNull String userId, @NonNull String placeId);

    void removeFromFavoriteRestaurant(@NonNull String userId, @NonNull String placeId);

    void setSelectedRestaurant(
        @NonNull String userId,
        @NonNull String placeId,
        @NonNull String restaurantName
    );

    void clearSelectedRestaurant(@NonNull String userId);

    @NonNull
    String getRoomId(@NonNull String userId1, @NonNull String userId2);

    @NonNull
    LiveData<ChatRoom> getChatRoom(@NonNull String roomId);

    void chatRoomExist(@NonNull String roomId, @NonNull OnChatRoomResult onChatRoomResult);

    void addChatRoom(@NonNull String roomId, @NonNull ChatRoom chatRoom);

    void addMessageToChat(@NonNull String roomId, @NonNull ChatRoom.Message message);

    void removeListenerRegistrations();

    interface OnChatRoomResult {

        void onResult(boolean exists);
    }
}
