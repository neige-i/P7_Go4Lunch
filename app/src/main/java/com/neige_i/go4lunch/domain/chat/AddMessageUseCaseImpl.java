package com.neige_i.go4lunch.domain.chat;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.ChatRoom;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

public class AddMessageUseCaseImpl implements AddMessageUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final Clock clock;

    @Inject
    AddMessageUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull Clock clock
    ) {
        this.firestoreRepository = firestoreRepository;
        this.firebaseAuth = firebaseAuth;
        this.clock = clock;
    }

    @Override
    public void add(@NonNull String workmateId, @NonNull String message) {
        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        final String roomId = firestoreRepository.getRoomId(currentUserId, workmateId);

        // Init the message to add
        final ChatRoom.Message messageToAdd = new ChatRoom.Message(
            message,
            LocalDateTime.now(clock).format(FirestoreRepository.DATE_TIME_FORMATTER),
            currentUserId
        );

        firestoreRepository.chatRoomExist(roomId, exists -> {
            if (exists) {
                // Only updates the chat room, if already exists
                firestoreRepository.addMessageToChat(roomId, messageToAdd);
            } else {
                // Add a new chat room with a single message in it
                firestoreRepository.addChatRoom(
                    roomId,
                    new ChatRoom(
                        Arrays.asList(currentUserId, workmateId),
                        Collections.singletonList(messageToAdd)
                    )
                );
            }
        });
    }
}
