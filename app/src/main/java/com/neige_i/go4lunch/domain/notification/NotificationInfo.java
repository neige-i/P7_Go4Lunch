package com.neige_i.go4lunch.domain.notification;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class NotificationInfo {

    @NonNull
    private final String restaurantId;
    @NonNull
    private final String restaurantName;
    @NonNull
    private final String restaurantAddress;
    @NonNull
    private final List<String> workmateNames;

    NotificationInfo(
        @NonNull String restaurantId, @NonNull String restaurantName,
        @NonNull String restaurantAddress,
        @NonNull List<String> workmateNames
    ) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.workmateNames = workmateNames;
    }

    @NonNull
    public String getRestaurantId() {
        return restaurantId;
    }

    @NonNull
    public String getRestaurantName() {
        return restaurantName;
    }

    @NonNull
    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    @NonNull
    public List<String> getWorkmateNames() {
        return workmateNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationInfo notificationInfo = (NotificationInfo) o;
        return restaurantId.equals(notificationInfo.restaurantId) &&
            restaurantName.equals(notificationInfo.restaurantName) &&
            restaurantAddress.equals(notificationInfo.restaurantAddress) &&
            workmateNames.equals(notificationInfo.workmateNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, restaurantName, restaurantAddress, workmateNames);
    }

    @NonNull
    @Override
    public String toString() {
        return "NotificationInfo{" +
            "restaurantId='" + restaurantId + '\'' +
            ", restaurantName='" + restaurantName + '\'' +
            ", restaurantAddress='" + restaurantAddress + '\'' +
            ", workmateNames=" + workmateNames +
            '}';
    }
}
