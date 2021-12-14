package com.neige_i.go4lunch.domain.chat;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class ChatInfo {

    @NonNull
    private final String workmateName;
    @NonNull
    private final List<MessageInfo> messageInfoList;

    ChatInfo(@NonNull String workmateName, @NonNull List<MessageInfo> messageInfoList) {
        this.workmateName = workmateName;
        this.messageInfoList = messageInfoList;
    }

    @NonNull
    public String getWorkmateName() {
        return workmateName;
    }

    @NonNull
    public List<MessageInfo> getMessageInfoList() {
        return messageInfoList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatInfo chatInfo = (ChatInfo) o;
        return workmateName.equals(chatInfo.workmateName) &&
            messageInfoList.equals(chatInfo.messageInfoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateName, messageInfoList);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatInfo{" +
            "workmateName='" + workmateName + '\'' +
            ", messageInfoList=" + messageInfoList +
            '}';
    }

    public static class MessageInfo {

        @NonNull
        private final String text;
        @NonNull
        private final String dateTime;
        private final boolean currentUserSender;

        public MessageInfo(
            @NonNull String text,
            @NonNull String dateTime,
            boolean currentUserSender
        ) {
            this.text = text;
            this.dateTime = dateTime;
            this.currentUserSender = currentUserSender;
        }

        @NonNull
        public String getText() {
            return text;
        }

        @NonNull
        public String getDateTime() {
            return dateTime;
        }

        public boolean isCurrentUserSender() {
            return currentUserSender;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MessageInfo that = (MessageInfo) o;
            return currentUserSender == that.currentUserSender && text.equals(that.text) && dateTime.equals(that.dateTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, dateTime, currentUserSender);
        }

        @NonNull
        @Override
        public String toString() {
            return "MessageInfo{" +
                "text='" + text + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", currentUserSender=" + currentUserSender +
                '}';
        }
    }
}
