package com.neige_i.go4lunch.domain.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.ChatRoom;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class GetChatInfoUseCaseImpl implements GetChatInfoUseCase {

    @NonNull
    private final MediatorLiveData<ChatInfo> chatInfo = new MediatorLiveData<>();

    @NonNull
    private final MutableLiveData<String> workmateIdMutableLiveData = new MutableLiveData<>();

    @NonNull
    private final String currentUserId;

    @Inject
    GetChatInfoUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth
    ) {

        if (firebaseAuth.getCurrentUser() == null) {
            currentUserId = "";
            return;
        }

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        final LiveData<ChatRoom> chatRoomLiveData = Transformations.switchMap(
            workmateIdMutableLiveData, workmateId -> {
                final String roomId = firestoreRepository.getRoomId(currentUserId, workmateId);
                return firestoreRepository.getChatRoom(roomId);
            }
        );
        final LiveData<User> workmateLiveData = Transformations.switchMap(
            workmateIdMutableLiveData, workmateId -> firestoreRepository.getUser(workmateId)
        );

        chatInfo.addSource(chatRoomLiveData, chatRoom -> combine(chatRoom, workmateLiveData.getValue()));
        chatInfo.addSource(workmateLiveData, workmate -> combine(chatRoomLiveData.getValue(), workmate));
    }

    private void combine(@Nullable ChatRoom chatRoom, @Nullable User workmate) {
        if (workmate == null) {
            return;
        }

        final List<ChatInfo.MessageInfo> messageInfoList;
        if (chatRoom != null) {
            messageInfoList = chatRoom.getMessages()
                .stream()
                .map(message -> {
                    return new ChatInfo.MessageInfo(
                        message.getText(),
                        message.getDateTime(),
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
        workmateIdMutableLiveData.setValue(workmateId);

        return chatInfo;
    }
}
