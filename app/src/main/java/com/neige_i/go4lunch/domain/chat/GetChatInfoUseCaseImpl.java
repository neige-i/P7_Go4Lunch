package com.neige_i.go4lunch.domain.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.Message;
import com.neige_i.go4lunch.data.firestore.User;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class GetChatInfoUseCaseImpl implements GetChatInfoUseCase {

    @NonNull
    private final MediatorLiveData<ChatInfo> chatInfo = new MediatorLiveData<>();
    @NonNull
    private final ZoneId systemDefaultZoneId;

    @NonNull
    private final MutableLiveData<String> workmateIdSource = new MutableLiveData<>();

    @NonNull
    private final String currentUserId;

    @Inject
    GetChatInfoUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull ZoneId systemDefaultZoneId
    ) {
        this.systemDefaultZoneId = systemDefaultZoneId;

        if (firebaseAuth.getCurrentUser() == null) {
            currentUserId = "";
            return;
        }

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        final LiveData<List<Message>> messagesLiveData = Transformations.switchMap(
            workmateIdSource, workmateId -> {
                final String roomId = firestoreRepository.getRoomId(currentUserId, workmateId);
                return firestoreRepository.getMessagesByRoomId(roomId);
            }
        );
        final LiveData<User> workmateLiveData = Transformations.switchMap(
            workmateIdSource, workmateId -> firestoreRepository.getUser(workmateId)
        );

        chatInfo.addSource(messagesLiveData, messages -> combine(messages, workmateLiveData.getValue()));
        chatInfo.addSource(workmateLiveData, workmate -> combine(messagesLiveData.getValue(), workmate));
    }

    private void combine(@Nullable List<Message> messages, @Nullable User workmate) {
        if (workmate == null) {
            return;
        }

        final List<ChatInfo.MessageInfo> messageInfoList;
        if (messages != null) {
            messageInfoList = messages
                .stream()
                .sorted(Comparator.comparingLong(message -> message.getDateTimeMillis()))
                .map(message -> {
                    return new ChatInfo.MessageInfo(
                        message.getText(),
                        Instant
                            .ofEpochMilli(message.getDateTimeMillis())
                            .atZone(systemDefaultZoneId)
                            .toLocalDateTime()
                            .format(FirestoreRepository.DATE_TIME_FORMATTER),
                        message.getSenderId().equals(currentUserId)
                    );
                })
                .collect(Collectors.toList());
        } else {
            messageInfoList = new ArrayList<>();
        }

        chatInfo.setValue(new ChatInfo(workmate.getName(), messageInfoList));
    }

    @NonNull
    @Override
    public LiveData<ChatInfo> get(@NonNull String workmateId) {
        // Trigger chatInfo Mediator's source
        workmateIdSource.setValue(workmateId);

        return chatInfo;
    }
}
