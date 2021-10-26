package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface WorkmatesDelegate {

    <T> void moveToFirstPosition(@NonNull List<T> list, Predicate<T> predicate);

    boolean isSelected(@Nullable String date);
}
