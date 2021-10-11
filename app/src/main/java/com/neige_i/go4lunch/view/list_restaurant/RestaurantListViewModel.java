package com.neige_i.go4lunch.view.list_restaurant;

import android.graphics.Typeface;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsListUseCase;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantListViewModel extends ViewModel {

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    @NonNull
    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HHmm");

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetRestaurantDetailsListUseCase getRestaurantDetailsListUseCase;

    @Inject
    public RestaurantListViewModel(@NonNull GetRestaurantDetailsListUseCase getRestaurantDetailsListUseCase) {
        this.getRestaurantDetailsListUseCase = getRestaurantDetailsListUseCase;
    }

    // ------------------------------------ VIEW STATE METHODS -------------------------------------

    @NonNull
    public LiveData<List<RestaurantViewState>> getViewState() {
        return Transformations.map(getRestaurantDetailsListUseCase.getDetailsList(), listWrapper -> {
            // The view state to return
            final List<RestaurantViewState> viewStates = new ArrayList<>();

            if (listWrapper != null) {
                for (NearbyRestaurant nearbyRestaurant : listWrapper.getNearbyRestaurants()) {
                    // 3. Setup the distance
                    final Location currentLocation = listWrapper.getCurrentLocation();
                    final float distance;
                    final String formattedDistance;
                    if (currentLocation == null) {
                        // TODO: remove this condition, should never happen
                        distance = Integer.MAX_VALUE; // View state list is sorted by ascending distance, this way "null" value will be at the end of the list
                        formattedDistance = "---m";
                    } else {
                        final float[] distances = new float[3];
                        Location.distanceBetween(
                            currentLocation.getLatitude(),
                            currentLocation.getLongitude(),
                            nearbyRestaurant.getLatitude(),
                            nearbyRestaurant.getLongitude(),
                            distances
                        );
                        distance = distances[0];
                        formattedDistance = getFormattedDistance(distance);
                    }

                    // GET opening hours
//                    final PlaceHourWrapper placeHourWrapper = getPlaceHour(listWrapper.getDetailsResponses(), nearbyRestaurant.getPlaceId());
                    final PlaceHourWrapper placeHourWrapper = new PlaceHourWrapper("Unknown hours", R.color.gray_dark, Typeface.NORMAL);

                    final Restaurant firestoreRestaurant = listWrapper.getRestaurants()
                        .stream()
                        .filter(restaurant -> restaurant.getRestaurantId().equals(nearbyRestaurant.getPlaceId()))
                        .findFirst().orElseGet(Restaurant::new);

                    // MAPPING
                    viewStates.add(new RestaurantViewState(
                        nearbyRestaurant.getPlaceId(),
                        nearbyRestaurant.getName(),
                        distance,
                        formattedDistance,
                        nearbyRestaurant.getAddress(),
                        placeHourWrapper.getFontStyle(),
                        placeHourWrapper.getFontColor(),
                        placeHourWrapper.getHours(),
                        firestoreRestaurant.getWorkmateId() != null,
                        firestoreRestaurant.getWorkmateId() != null ? 1 : 0,
                        nearbyRestaurant.getRating(),
                        nearbyRestaurant.getRating() == -1,
                        nearbyRestaurant.getPhotoUrl() != null ? nearbyRestaurant.getPhotoUrl() : "" // TODO: replace
                    ));

                    // SORT the restaurant list by distance in ascending order
                    Collections.sort(viewStates, (viewState1, viewState2) ->
                        (int) (viewState1.getDistance() - viewState2.getDistance()));
                }
            }

            return viewStates;
        });
    }

//    private PlaceHourWrapper getPlaceHour(@Nullable List<DetailsRestaurant> detailsRestaurants, @Nullable String nearbyPlaceId) {
//        final PlaceHourWrapper unknownHours = new PlaceHourWrapper("Unknown hours", R.color.gray_dark, Typeface.NORMAL);
//
//        if (detailsRestaurants == null || nearbyPlaceId == null) {
//            return unknownHours;
//        }
//
//        final List<RawDetailsResponse.Period> periodList = new ArrayList<>();
//        for (DetailsRestaurant detailsRestaurant : detailsRestaurants) {
//            if (detailsRestaurant != null) {
//
//                final String detailsPlaceId = detailsRestaurant.getPlaceId();
//                if (detailsPlaceId != null && detailsPlaceId.equals(nearbyPlaceId)) {
//
//                    if (detailsRestaurant.getResult().getOpeningHours() != null) {
//                        periodList.addAll(detailsRestaurant.getResult().getOpeningHours().getPeriods());
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (periodList.isEmpty()) {
//            return unknownHours;
//        }
//
//        // 1. Check if the place is never closed
//        if (periodList.stream().allMatch(period -> period.getClose() == null)) {
//            return new PlaceHourWrapper("Open 24/7", R.color.lime_dark, Typeface.ITALIC);
//        }
//
//        // Day values: from 1 (Monday) to 7 (Sunday) for java.time and from 0 (Sunday) to 6 (Saturday) for Places API
//        final DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
//        final int currentDay = dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();
//
//        // 2. Find the first opened period for today
//        RawDetailsResponse.Period firstTodayOpenPeriod = null;
//        for (RawDetailsResponse.Period period : periodList) {
//            if (period.getOpen() != null && period.getOpen().getDay() != null && period.getOpen().getDay() == currentDay) {
//                firstTodayOpenPeriod = period;
//                break;
//            }
//        }
//
//        // 3. Check if the place is opened today
//        if (firstTodayOpenPeriod == null) {
//            return new PlaceHourWrapper("Closed today", android.R.color.holo_red_dark, Typeface.BOLD);
//        }
//
//        PlaceHourWrapper placeHourWrapper = null;
//        for (int i = periodList.indexOf(firstTodayOpenPeriod); i < periodList.size(); i++) {
//
//            final RawDetailsResponse.Open todayOpen = periodList.get(i).getOpen();
//            if (todayOpen == null) {
//                break; // Should never happen because at this state we know the place is opened today
//            } else if (todayOpen.getDay() != null && todayOpen.getDay() != currentDay) {
//                break; // Break the loop if iterate over a period with an opened day different from today
//            }
//
//            final LocalTime currentTime = LocalTime.now();
//
//            // 4. Check if the place isn't opened yet
//            final LocalTime openTime = LocalTime.parse(todayOpen.getTime(), HOUR_FORMAT);
//            if (currentTime.isBefore(openTime)) {
//                // TODO: handle when next open is another day
//                placeHourWrapper = new PlaceHourWrapper("Closed until " + openTime, android.R.color.holo_red_dark, Typeface.BOLD);
//                break;
//            }
//
//            if (firstTodayOpenPeriod.getClose() == null) {
//                break;
//            }
//
//            final LocalTime closeTime = LocalTime.parse(firstTodayOpenPeriod.getClose().getTime(), HOUR_FORMAT);
//
//            final int closedNextDay;
//            if (firstTodayOpenPeriod.getClose().getDay() != null && firstTodayOpenPeriod.getClose().getDay() != currentDay) {
//                closedNextDay = 24; // 24 hours a day
//            } else {
//                closedNextDay = 0;
//            }
//
//            if (currentTime.isBefore(closeTime) || closedNextDay != 0) {
//                if (Duration.between(currentTime, closeTime).toHours() + closedNextDay < 1) {
//                    placeHourWrapper = new PlaceHourWrapper("Closing soon", android.R.color.holo_red_dark, Typeface.BOLD);
//                } else {
//                    placeHourWrapper = new PlaceHourWrapper("Open until " + closeTime, R.color.lime_dark, Typeface.ITALIC);
//                }
//                break;
//            }
//        }
//
//        return placeHourWrapper == null ? unknownHours : placeHourWrapper;
//    }

    private String getFormattedDistance(float originalDistance) {
        return originalDistance < 1000
            ? String.format(Locale.getDefault(), "%.0fm", originalDistance)
            : String.format(Locale.getDefault(), "%.2fkm", originalDistance / 1000);
    }
}
