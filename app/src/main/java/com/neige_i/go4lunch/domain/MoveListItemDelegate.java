package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.function.Predicate;

public interface MoveListItemDelegate {

    <T> void toFirstPosition(@NonNull List<T> list, Predicate<T> predicate);
}
