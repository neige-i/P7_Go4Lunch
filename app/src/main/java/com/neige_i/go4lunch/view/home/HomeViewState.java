package com.neige_i.go4lunch.view.home;

import androidx.annotation.StringRes;

import java.util.Objects;

class HomeViewState {

    @StringRes
    private final int titleId;
    private final int viewPagerPosition;

    HomeViewState(int titleId, int viewPagerPosition) {
        this.titleId = titleId;
        this.viewPagerPosition = viewPagerPosition;
    }

    @StringRes
    public int getTitleId() {
        return titleId;
    }

    public int getViewPagerPosition() {
        return viewPagerPosition;
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
            viewPagerPosition == that.viewPagerPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleId, viewPagerPosition);
    }
}
