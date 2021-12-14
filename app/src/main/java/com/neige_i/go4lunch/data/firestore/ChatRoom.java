package com.neige_i.go4lunch.data.firestore;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

@SuppressWarnings({"ConstantConditions", "unused"})
public class ChatRoom {

    @NonNull
    private final List<String> participants;
    @NonNull
    private final List<Message> messages;

    public ChatRoom() {
        participants = null;
        messages = null;
    }

    public ChatRoom(@NonNull List<String> participants, @NonNull List<Message> messages) {
        this.participants = participants;
        this.messages = messages;
    }

    @NonNull
    public List<String> getParticipants() {
        return participants;
    }

    @NonNull
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatRoom chatRoom = (ChatRoom) o;
        return participants.equals(chatRoom.participants) &&
            messages.equals(chatRoom.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participants, messages);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatRoom{" +
            ", participants=" + participants +
            ", messages=" + messages +
            '}';
    }

    public static class Message {

        @NonNull
        private final String text;
        @NonNull
        private final String dateTime;
        @NonNull
        private final String senderId;

        public Message() {
            text = null;
            dateTime = null;
            senderId = null;
        }

        public Message(
            @NonNull String text,
            @NonNull String dateTime,
            @NonNull String senderId
        ) {
            this.text = text;
            this.dateTime = dateTime;
            this.senderId = senderId;
        }

        @NonNull
        public String getText() {
            return text;
        }

        @NonNull
        public String getDateTime() {
            return dateTime;
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
            return text.equals(message.text) &&
                dateTime.equals(message.dateTime) &&
                senderId.equals(message.senderId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, dateTime, senderId);
        }

        @NonNull
        @Override
        public String toString() {
            return "Message{" +
                "text='" + text + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", senderId='" + senderId + '\'' +
                '}';
        }
    }
}
