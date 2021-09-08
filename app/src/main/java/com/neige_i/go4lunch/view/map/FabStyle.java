package com.neige_i.go4lunch.view.map;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.Objects;

class FabStyle {

    @DrawableRes
    private final int fabDrawable;
    @ColorRes
    private final int fabColor;

    FabStyle(int fabDrawable, int fabColor) {
        this.fabDrawable = fabDrawable;
        this.fabColor = fabColor;
    }

    public int getFabDrawable() {
        return fabDrawable;
    }

    public int getFabColor() {
        return fabColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FabStyle fabStyle = (FabStyle) o;
        return fabDrawable == fabStyle.fabDrawable && fabColor == fabStyle.fabColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fabDrawable, fabColor);
    }

    @NonNull
    @Override
    public String toString() {
        return "FabStyle{" +
            "fabDrawable=" + fabDrawable +
            ", fabColor=" + fabColor +
            '}';
    }
}
