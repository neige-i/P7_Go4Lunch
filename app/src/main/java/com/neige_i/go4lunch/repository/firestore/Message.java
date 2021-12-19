package com.neige_i.go4lunch.repository.firestore;

import androidx.annotation.NonNull;

import java.util.Objects;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Message {

    @NonNull
    private final String roomId;
    @NonNull
    private final String text;
    private final long dateTimeMillis;
    @NonNull
    private final String senderId;

    public Message() {
        roomId = null;
        text = null;
        dateTimeMillis = 0;
        senderId = null;
    }

    public Message(
        @NonNull String roomId,
        @NonNull String text,
        long dateTimeMillis,
        @NonNull String senderId
    ) {
        this.roomId = roomId;
        this.text = text;
        this.dateTimeMillis = dateTimeMillis;
        this.senderId = senderId;
    }

    @NonNull
    public String getRoomId() {
        return roomId;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public long getDateTimeMillis() {
        return dateTimeMillis;
    }

    @NonNull
    public String getSenderId() {
        return senderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return dateTimeMillis == message.dateTimeMillis &&
            roomId.equals(message.roomId) &&
            text.equals(message.text) &&
            senderId.equals(message.senderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, text, dateTimeMillis, senderId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
            "roomId='" + roomId + '\'' +
            ", text='" + text + '\'' +
            ", dateTimeMillis=" + dateTimeMillis +
            ", senderId='" + senderId + '\'' +
            '}';
    }
}
