package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Objects;

class HomeViewState {

    @StringRes
    private final int titleId;
    private final int viewPagerPosition;
    private final boolean isSearchEnabled;

    HomeViewState(
        int titleId,
        int viewPagerPosition,
        boolean isSearchEnabled
    ) {
        this.titleId = titleId;
        this.viewPagerPosition = viewPagerPosition;
        this.isSearchEnabled = isSearchEnabled;
    }

    @StringRes
    public int getTitleId() {
        return titleId;
    }

    public int getViewPagerPosition() {
        return viewPagerPosition;
    }

    public boolean isSearchEnabled() {
        return isSearchEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HomeViewState that = (HomeViewState) o;
        return titleId == that.titleId &&
            viewPagerPosition == that.viewPagerPosition &&
            isSearchEnabled == that.isSearchEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleId, viewPagerPosition, isSearchEnabled);
    }

    @NonNull
    @Override
    public String toString() {
        return "HomeViewState{" +
            "titleId=" + titleId +
            ", viewPagerPosition=" + viewPagerPosition +
            ", isSearchEnabled=" + isSearchEnabled +
            '}';
    }
}
