package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;

public class MoveListItemDelegateImpl implements MoveListItemDelegate {

    @Inject
    public MoveListItemDelegateImpl() {
    }

    @Override
    public <T> void toFirstPosition(
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
}
