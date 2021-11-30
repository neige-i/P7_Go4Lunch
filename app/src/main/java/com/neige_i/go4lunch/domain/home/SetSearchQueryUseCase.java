package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;

public interface SetSearchQueryUseCase {

    void launch(@NonNull String searchQuery);

    void close();
}
