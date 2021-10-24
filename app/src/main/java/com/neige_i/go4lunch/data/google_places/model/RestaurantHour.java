package com.neige_i.go4lunch.data.google_places.model;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class RestaurantHour {

    private final boolean open;
    @NonNull
    private final DayOfWeek dayOfWeek;
    @NonNull
    private final LocalTime localTime;

    public RestaurantHour(
        boolean open,
        @NonNull DayOfWeek dayOfWeek,
        @NonNull LocalTime localTime
    ) {
        this.open = open;
        this.dayOfWeek = dayOfWeek;
        this.localTime = localTime;
    }

    public boolean isOpen() {
        return open;
    }

    @NonNull
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @NonNull
    public LocalTime getLocalTime() {
        return localTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestaurantHour that = (RestaurantHour) o;
        return open == that.open &&
            dayOfWeek == that.dayOfWeek &&
            localTime.equals(that.localTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, dayOfWeek, localTime);
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
            (open ? "opens" : "closes") +
            " " + dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()) +
            " at " + localTime +
            '}';
    }
}
