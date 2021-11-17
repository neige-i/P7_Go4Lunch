package com.neige_i.go4lunch.view.list_restaurant;

import android.app.Application;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.list_restaurant.GetNearbyDetailsUseCase;
import com.neige_i.go4lunch.domain.list_restaurant.HourResult;
import com.neige_i.go4lunch.domain.list_restaurant.NearbyDetail;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantListViewModel extends ViewModel {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetNearbyDetailsUseCase getNearbyDetailsUseCase;
    @NonNull
    private final Application application;
    @NonNull
    private final Locale defaultLocale;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public RestaurantListViewModel(
        @NonNull GetNearbyDetailsUseCase getNearbyDetailsUseCase,
        @NonNull Application application,
        @NonNull Locale defaultLocale
    ) {
        this.getNearbyDetailsUseCase = getNearbyDetailsUseCase;
        this.application = application;
        this.defaultLocale = defaultLocale;
    }

    // ------------------------------------ VIEW STATE METHODS -------------------------------------

    @NonNull
    public LiveData<List<RestaurantViewState>> getViewState() {
        return Transformations.map(getNearbyDetailsUseCase.get(), nearbyDetails -> {
            final List<RestaurantViewState> restaurantViewStates = new ArrayList<>();

            for (NearbyDetail nearbyDetail : nearbyDetails) {
                final float distance = nearbyDetail.getDistance();

                // Setup formatted distance
                final boolean isLessThan1Km = distance < 1000;
                final String formattedDistance = String.format(
                    defaultLocale,
                    isLessThan1Km ? "%.0fm" : "%.2fkm",
                    isLessThan1Km ? distance : distance / 1000
                );

                // Setup opening hours
                final HourResult hourResult = nearbyDetail.getHourResult();
                final String hourText;
                final int hourTextColor;
                final int hourTextStyle;
                if (hourResult instanceof HourResult.Loading) {
                    hourText = "...";
                    hourTextColor = R.color.gray_dark;
                    hourTextStyle = Typeface.NORMAL;
                } else if (hourResult instanceof HourResult.Unknown) {
                    hourText = application.getString(R.string.unknown_hours);
                    hourTextColor = R.color.gray_dark;
                    hourTextStyle = Typeface.NORMAL;
                } else if (hourResult instanceof HourResult.AlwaysOpen) {
                    hourText = application.getString(R.string.always_open);
                    hourTextColor = R.color.lime_dark;
                    hourTextStyle = Typeface.ITALIC;
                } else if (hourResult instanceof HourResult.ClosingSoon) {
                    hourText = application.getString(R.string.closing_soon);
                    hourTextColor = android.R.color.holo_red_dark;
                    hourTextStyle = Typeface.BOLD;
                } else if (hourResult instanceof HourResult.Open) {
                    final LocalDateTime nextClosingHour = ((HourResult.Open) hourResult).getNextClosingHour();
                    final String closingWeekDay = getWeekDay(((HourResult.Open) hourResult).getDayDiff(), nextClosingHour);
                    hourText = application.getString(R.string.open_until, closingWeekDay, nextClosingHour.toLocalTime());
                    hourTextColor = R.color.lime_dark;
                    hourTextStyle = Typeface.ITALIC;
                } else if (hourResult instanceof HourResult.Closed) {
                    final LocalDateTime nextOpeningHour = ((HourResult.Closed) hourResult).getNextOpeningHour();
                    final String openingWeekDay = getWeekDay(((HourResult.Closed) hourResult).getDayDiff(), nextOpeningHour);
                    hourText = application.getString(R.string.closed_until, openingWeekDay, nextOpeningHour.toLocalTime());
                    hourTextColor = android.R.color.holo_red_dark;
                    hourTextStyle = Typeface.BOLD;
                } else {
                    throw new IllegalArgumentException(hourResult.getClass().getSimpleName() + " is a wrong implementation");
                }

                restaurantViewStates.add(new RestaurantViewState(
                    nearbyDetail.getPlaceId(),
                    nearbyDetail.getRestaurantName(),
                    nearbyDetail.getAddress(),
                    distance,
                    formattedDistance,
                    hourText,
                    hourTextStyle,
                    hourTextColor,
                    nearbyDetail.getInterestedWorkmatesCount(),
                    nearbyDetail.getRating(),
                    nearbyDetail.getPhotoUrl()
                ));

                // Sort the restaurant list by distance in ascending order
                Collections.sort(restaurantViewStates, (viewState1, viewState2) -> {
                    return Float.compare(viewState1.getDistance(), viewState2.getDistance());
                });
            }

            return restaurantViewStates;
        });
    }

    @NonNull
    private String getWeekDay(int dayDiff, LocalDateTime nextHour) {
        switch (dayDiff) {
            case 0:
                return "";
            case 1:
                return application.getString(R.string.tomorrow) + " ";
            default:
                return nextHour.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, defaultLocale) + " ";
        }
    }
}
