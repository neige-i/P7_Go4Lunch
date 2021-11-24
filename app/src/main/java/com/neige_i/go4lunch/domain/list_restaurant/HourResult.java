package com.neige_i.go4lunch.domain.list_restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class HourResult {

    public static class Loading extends HourResult {
        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof Loading;
        }

        @NonNull
        @Override
        public String toString() {
            return "Loading{}";
        }
    }

    public static class Unknown extends HourResult {
        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof Unknown;
        }

        @NonNull
        @Override
        public String toString() {
            return "Unknown{}";
        }
    }

    public static class AlwaysOpen extends HourResult {
        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof AlwaysOpen;
        }

        @NonNull
        @Override
        public String toString() {
            return "AlwaysOpen{}";
        }
    }

    public static class Open extends HourResult {

        private final int dayDiff;
        @NonNull
        private final LocalDateTime nextClosingHour;

        public Open(int dayDiff, @NonNull LocalDateTime nextClosingHour) {
            this.dayDiff = dayDiff;
            this.nextClosingHour = nextClosingHour;
        }

        public int getDayDiff() {
            return dayDiff;
        }

        @NonNull
        public LocalDateTime getNextClosingHour() {
            return nextClosingHour;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Open open = (Open) o;
            return dayDiff == open.dayDiff && nextClosingHour.equals(open.nextClosingHour);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dayDiff, nextClosingHour);
        }

        @NonNull
        @Override
        public String toString() {
            return "Open{" +
                "dayDiff=" + dayDiff +
                ", nextClosingHour=" + nextClosingHour +
                '}';
        }
    }

    public static class ClosingSoon extends HourResult {
        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof ClosingSoon;
        }

        @NonNull
        @Override
        public String toString() {
            return "ClosingSoon{}";
        }
    }

    public static class Closed extends HourResult {

        private final int dayDiff;
        @NonNull
        private final LocalDateTime nextOpeningHour;

        public Closed(int dayDiff, @NonNull LocalDateTime nextOpeningHour) {
            this.dayDiff = dayDiff;
            this.nextOpeningHour = nextOpeningHour;
        }

        public int getDayDiff() {
            return dayDiff;
        }

        @NonNull
        public LocalDateTime getNextOpeningHour() {
            return nextOpeningHour;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Closed closed = (Closed) o;
            return dayDiff == closed.dayDiff && nextOpeningHour.equals(closed.nextOpeningHour);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dayDiff, nextOpeningHour);
        }

        @NonNull
        @Override
        public String toString() {
            return "Closed{" +
                "dayDiff=" + dayDiff +
                ", nextOpeningHour=" + nextOpeningHour +
                '}';
        }
    }
}
