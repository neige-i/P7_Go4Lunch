package com.neige_i.go4lunch.domain.list_restaurant;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

public abstract class HourResult {

    public static class Loading extends HourResult {
    }

    public static class Unknown extends HourResult {
    }

    public static class AlwaysOpen extends HourResult {
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
    }

    public static class ClosingSoon extends HourResult {
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
    }
}
