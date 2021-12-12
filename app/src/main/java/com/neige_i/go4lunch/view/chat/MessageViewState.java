package com.neige_i.go4lunch.view.chat;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import java.util.Objects;

class MessageViewState {

    @NonNull
    private final String message;
    @NonNull
    private final String dateTime;
    @ColorRes
    private final int backgroundColor;
    private final float horizontalBias;

    MessageViewState(
        @NonNull String message,
        @NonNull String dateTime,
        int backgroundColor,
        float horizontalBias
    ) {
        this.message = message;
        this.dateTime = dateTime;
        this.backgroundColor = backgroundColor;
        this.horizontalBias = horizontalBias;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageViewState that = (MessageViewState) o;
        return backgroundColor == that.backgroundColor && Float.compare(that.horizontalBias, horizontalBias) == 0 && message.equals(that.message) && dateTime.equals(that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, dateTime, backgroundColor, horizontalBias);
    }

    @NonNull
    @Override
    public String toString() {
        return "MessageViewState{" +
            "message='" + message + '\'' +
            ", dateTime='" + dateTime + '\'' +
            ", backgroundColor=" + backgroundColor +
            ", horizontalBias=" + horizontalBias +
            '}';
    }
}
