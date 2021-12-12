package com.neige_i.go4lunch.view.chat;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

class ChatViewState {

    @NonNull
    private final String workmateName;
    @NonNull
    private final List<MessageViewState> messageViewStates;

    ChatViewState(
        @NonNull String workmateName,
        @NonNull List<MessageViewState> messageViewStates
    ) {
        this.workmateName = workmateName;
        this.messageViewStates = messageViewStates;
    }

    @NonNull
    public String getWorkmateName() {
        return workmateName;
    }

    @NonNull
    public List<MessageViewState> getMessageViewStates() {
        return messageViewStates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatViewState that = (ChatViewState) o;
        return workmateName.equals(that.workmateName) &&
            messageViewStates.equals(that.messageViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateName, messageViewStates);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatViewState{" +
            "workmateName='" + workmateName + '\'' +
            ", messageViewStates=" + messageViewStates +
            '}';
    }
}
