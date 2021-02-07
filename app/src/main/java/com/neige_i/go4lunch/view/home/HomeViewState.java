package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class HomeViewState {

    @NonNull
    private final String fragmentToShow;
    @Nullable
    private final String fragmentToHide;
    private final int titleId;

    public HomeViewState(@NonNull String fragmentToShow, @Nullable String fragmentToHide, int titleId) {
        this.fragmentToShow = fragmentToShow;
        this.fragmentToHide = fragmentToHide;
        this.titleId = titleId;
    }

    @NonNull
    public String getFragmentToShow() {
        return fragmentToShow;
    }

    @Nullable
    public String getFragmentToHide() {
        return fragmentToHide;
    }

    public int getTitleId() {
        return titleId;
    }
}
