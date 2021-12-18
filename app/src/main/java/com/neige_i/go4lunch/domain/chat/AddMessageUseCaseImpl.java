package com.neige_i.go4lunch.domain.chat;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.Message;

import java.time.Clock;
import java.time.ZonedDateTime;

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

        firestoreRepository.addMessage(
            new Message(
                firestoreRepository.getRoomId(currentUserId, workmateId),
                message,
                ZonedDateTime.now(clock).toInstant().toEpochMilli(),
                currentUserId
            )
        );
    }
}
