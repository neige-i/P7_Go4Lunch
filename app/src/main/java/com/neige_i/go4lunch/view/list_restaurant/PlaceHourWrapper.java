package com.neige_i.go4lunch.view.list_restaurant;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

class PlaceHourWrapper {

    @NonNull
    private final String hours;
    @IdRes
    private final int fontColor;
    @IdRes
    private final int fontStyle;

    PlaceHourWrapper(@NonNull String hours, int fontColor, int fontStyle) {
        this.hours = hours;
        this.fontColor = fontColor;
        this.fontStyle = fontStyle;
    }

    @NonNull
    public String getHours() {
        return hours;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getFontStyle() {
        return fontStyle;
    }
}
