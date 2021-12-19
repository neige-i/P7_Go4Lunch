package com.neige_i.go4lunch.view.chat;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

class ChatViewState {

    @NonNull
    private final String workmateName;
    @NonNull
    private final List<MessageViewState> messageViewStates;
    private final boolean textViewVisible;
    private final boolean fabEnabled;
    private final float fabAlpha;
    private final boolean scrollBottomButtonVisible;

    ChatViewState(
        @NonNull String workmateName,
        @NonNull List<MessageViewState> messageViewStates,
        boolean textViewVisible,
        boolean fabEnabled,
        float fabAlpha,
        boolean scrollBottomButtonVisible
    ) {
        this.workmateName = workmateName;
        this.messageViewStates = messageViewStates;
        this.textViewVisible = textViewVisible;
        this.fabEnabled = fabEnabled;
        this.fabAlpha = fabAlpha;
        this.scrollBottomButtonVisible = scrollBottomButtonVisible;
    }

    @NonNull
    public String getWorkmateName() {
        return workmateName;
    }

    @NonNull
    public List<MessageViewState> getMessageViewStates() {
        return messageViewStates;
    }

    public boolean isTextViewVisible() {
        return textViewVisible;
    }

    public boolean isFabEnabled() {
        return fabEnabled;
    }

    public float getFabAlpha() {
        return fabAlpha;
    }

    public boolean isScrollBottomButtonVisible() {
        return scrollBottomButtonVisible;
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
        return textViewVisible == that.textViewVisible &&
            fabEnabled == that.fabEnabled &&
            fabAlpha == that.fabAlpha &&
            scrollBottomButtonVisible == that.scrollBottomButtonVisible &&
            workmateName.equals(that.workmateName) &&
            messageViewStates.equals(that.messageViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateName, messageViewStates, textViewVisible, fabEnabled, fabAlpha, scrollBottomButtonVisible);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatViewState{" +
            "workmateName='" + workmateName + '\'' +
            ", messageViewStates=" + messageViewStates +
            ", textViewVisible='" + textViewVisible + '\'' +
            ", fabEnabled='" + fabEnabled + '\'' +
            ", fabAlpha='" + fabAlpha + '\'' +
            ", scrollBottomButtonVisible='" + scrollBottomButtonVisible + '\'' +
            '}';
    }

    static class MessageViewState {

        @NonNull
        private final String message;
        @NonNull
        private final String dateTime;
        @ColorRes
        private final int backgroundColor;
        private final float horizontalBias;
        private final int marginStart;
        private final int marginEnd;

        MessageViewState(
            @NonNull String message,
            @NonNull String dateTime,
            int backgroundColor,
            float horizontalBias,
            int marginStart,
            int marginEnd
        ) {
            this.message = message;
            this.dateTime = dateTime;
            this.backgroundColor = backgroundColor;
            this.horizontalBias = horizontalBias;
            this.marginStart = marginStart;
            this.marginEnd = marginEnd;
        }

        @NonNull
        public String getMessage() {
            return message;
        }

        @NonNull
        public String getDateTime() {
            return dateTime;
        }

        @ColorRes
        public int getBackgroundColor() {
            return backgroundColor;
        }

        public float getHorizontalBias() {
            return horizontalBias;
        }

        public int getMarginStart() {
            return marginStart;
        }

        public int getMarginEnd() {
            return marginEnd;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MessageViewState that = (MessageViewState) o;
            return backgroundColor == that.backgroundColor &&
                marginStart == that.marginStart &&
                marginEnd == that.marginEnd &&
                Float.compare(that.horizontalBias, horizontalBias) == 0 &&
                message.equals(that.message) &&
                dateTime.equals(that.dateTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, dateTime, backgroundColor, horizontalBias, marginStart, marginEnd);
        }

        @NonNull
        @Override
        public String toString() {
            return "MessageViewState{" +
                "message='" + message + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", backgroundColor=" + backgroundColor +
                ", horizontalBias=" + horizontalBias +
                ", marginStart=" + marginStart +
                ", marginEnd=" + marginEnd +
                '}';
        }
    }
}
