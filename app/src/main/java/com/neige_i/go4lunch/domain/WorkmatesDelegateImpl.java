package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.firestore.FirestoreRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;

public class WorkmatesDelegateImpl implements WorkmatesDelegate {

    @NonNull
    private final Clock clock;

    @Inject
    public WorkmatesDelegateImpl(@NonNull Clock clock) {
        this.clock = clock;
    }

    @Override
    public <T> void moveToFirstPosition(
        @NonNull List<T> list, Predicate<T> predicate
    ) {
        // Find the item matching the predicate
        final T item = list.stream()
            .filter(predicate)
            .findFirst()
            .orElse(null);

        // Put the item in first position
        if (list.remove(item)) {
            list.add(0, item);
        }
    }

    @Override
    public boolean isSelected(@Nullable String date) {
        if (date == null) {
            return false;
        }

        return LocalDate
            .parse(date, FirestoreRepository.DATE_FORMATTER)
            .isEqual(LocalDate.now(clock));
    }
}
