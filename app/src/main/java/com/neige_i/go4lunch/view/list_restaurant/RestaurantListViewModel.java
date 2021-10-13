package com.neige_i.go4lunch.view.list_restaurant;

import android.graphics.Typeface;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.firebase.model.Restaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
import com.neige_i.go4lunch.domain.firestore.GetNearbyFirestoreRestaurantsUseCase;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantDetailsUseCase;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantsUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationUseCase;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantListViewModel extends ViewModel {

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    @NonNull
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final Clock clock;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<List<RestaurantViewState>> restaurantsViewState = new MediatorLiveData<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    public RestaurantListViewModel(
        @NonNull GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase,
        @NonNull GetNearbyRestaurantDetailsUseCase getNearbyRestaurantDetailsUseCase,
        @NonNull GetLocationUseCase getLocationUseCase,
        @NonNull GetNearbyFirestoreRestaurantsUseCase getNearbyFirestoreRestaurantsUseCase,
        @NonNull Clock clock
    ) {
        this.clock = clock;

        final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData = getNearbyRestaurantsUseCase.get();
        final LiveData<Map<String, RestaurantDetails>> restaurantsDetailsLiveData = getNearbyRestaurantDetailsUseCase.get();
        final LiveData<Location> locationLiveData = getLocationUseCase.get();
        final LiveData<List<Restaurant>> firestoreRestaurantListLiveData = getNearbyFirestoreRestaurantsUseCase.get();

        restaurantsViewState.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> combine(nearbyRestaurants, restaurantsDetailsLiveData.getValue(), locationLiveData.getValue(), firestoreRestaurantListLiveData.getValue()));
        restaurantsViewState.addSource(restaurantsDetailsLiveData, restaurantDetails -> combine(nearbyRestaurantsLiveData.getValue(), restaurantDetails, locationLiveData.getValue(), firestoreRestaurantListLiveData.getValue()));
        restaurantsViewState.addSource(locationLiveData, location -> combine(nearbyRestaurantsLiveData.getValue(), restaurantsDetailsLiveData.getValue(), location, firestoreRestaurantListLiveData.getValue()));
        restaurantsViewState.addSource(firestoreRestaurantListLiveData, firestoreRestaurantList -> combine(nearbyRestaurantsLiveData.getValue(), restaurantsDetailsLiveData.getValue(), locationLiveData.getValue(), firestoreRestaurantList));
    }

    private void combine(
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @Nullable Map<String, RestaurantDetails> restaurantsDetails,
        @Nullable Location currentLocation,
        @Nullable List<Restaurant> firestoreRestaurantList
    ) {
        if (nearbyRestaurants == null || currentLocation == null) {
            return;
        }

        final List<RestaurantViewState> viewStates = new ArrayList<>();

        for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
            final String restaurantId = nearbyRestaurant.getPlaceId();

            final float distance = computeDistance(currentLocation, nearbyRestaurant);

            final PlaceHourWrapper placeHourWrapper = getPlaceHour(restaurantsDetails, restaurantId);
//            final PlaceHourWrapper placeHourWrapper = new PlaceHourWrapper("Unknown hours", R.color.gray_dark, Typeface.NORMAL);

            viewStates.add(new RestaurantViewState(
                restaurantId,
                nearbyRestaurant.getName(),
                distance,
                getFormattedDistance(distance),
                nearbyRestaurant.getAddress(),
                placeHourWrapper.getFontStyle(),
                placeHourWrapper.getFontColor(),
                placeHourWrapper.getHours(),
                computeInterestedWorkmateCount(firestoreRestaurantList, restaurantId),
                nearbyRestaurant.getRating(),
                nearbyRestaurant.getRating() == -1,
                nearbyRestaurant.getPhotoUrl() != null ? nearbyRestaurant.getPhotoUrl() : "" // TODO: replace
            ));

            // SORT the restaurant list by distance in ascending order
            Collections.sort(viewStates, (viewState1, viewState2) ->
                (int) (viewState1.getDistance() - viewState2.getDistance()));
        }

        restaurantsViewState.setValue(viewStates);
    }

    private float computeDistance(
        @NonNull Location currentLocation,
        @NonNull NearbyRestaurant nearbyRestaurant
    ) {
        final float[] distances = new float[3];

        Location.distanceBetween(
            currentLocation.getLatitude(),
            currentLocation.getLongitude(),
            nearbyRestaurant.getLatitude(),
            nearbyRestaurant.getLongitude(),
            distances
        );

        return distances[0];
    }

    @NonNull
    private String getFormattedDistance(float originalDistance) {
        final boolean isLessThan1Km = originalDistance < 1000;
        return String.format(
            Locale.getDefault(),
            isLessThan1Km ? "%.0fm" : "%.2fkm",
            isLessThan1Km ? originalDistance : originalDistance / 1000
        );
    }

    private int computeInterestedWorkmateCount(
        @Nullable List<Restaurant> firestoreRestaurantList,
        @NonNull String restaurantId
    ) {
        if (firestoreRestaurantList == null) {
            return 0;
        }

        Log.d("Neige", "computeInterestedWorkmateCount: restaurant=" + firestoreRestaurantList);
        final Restaurant firestoreRestaurant = firestoreRestaurantList.stream()
            .filter(restaurant -> Objects.equals(restaurant.getRestaurantId(), restaurantId))
            .findFirst()
            .orElse(null);

        if (firestoreRestaurant == null || firestoreRestaurant.getWorkmateMap() == null) {
            return 0;
        } else {
            return firestoreRestaurant.getWorkmateMap().size(); // TODO: filter to get only today's interested workmates
        }
    }

    @NonNull
    public LiveData<List<RestaurantViewState>> getViewState() {
        return restaurantsViewState;
    }

    // ------------------------------------ VIEW STATE METHODS -------------------------------------

    @NonNull
    private PlaceHourWrapper getPlaceHour(@Nullable Map<String, RestaurantDetails> restaurantDetailsMap, @NonNull String restaurantId) {
        if (restaurantDetailsMap == null) {
            return new PlaceHourWrapper("...", R.color.gray_dark, Typeface.NORMAL);
        }

        final RestaurantDetails restaurantDetails = restaurantDetailsMap.get(restaurantId);
        if (restaurantDetails == null) {
            return new PlaceHourWrapper("...", R.color.gray_dark, Typeface.NORMAL);
        }

        final List<String> openingHours = restaurantDetails.getOpeningHours();
        if (openingHours == null || openingHours.isEmpty()) {
            return new PlaceHourWrapper("Unknown hours", R.color.gray_dark, Typeface.NORMAL);
        }

        Log.d("Neige", "getPlaceHour: " + openingHours);
        if (openingHours.size() == 2 &&
            openingHours.get(0).equals("70000") && // getDay() has been converted in the repository
            openingHours.get(1) == null
        ) {
            return new PlaceHourWrapper("Open 24/7", R.color.lime_dark, Typeface.ITALIC);
        }

        final LocalDateTime localDateTime = LocalDateTime.now(clock);
        // Day starts at Monday=1 for java.time and at Sunday=0 for Places API
//        final DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
//        final int currentDay = dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();

//        int openingHoursIndex = 0;
//        for (String openingHour : openingHours) {
//            if (!localDateTime.isBefore(LocalDateTime.parse(openingHour, DATE_TIME_FORMATTER))) {
//                break;
//            }
//            openingHoursIndex++;
//        }

        return new PlaceHourWrapper("Other", R.color.gray_dark, Typeface.NORMAL);
//        if () {
//
//        }

        // TODO: handle when next open is another day
//        new PlaceHourWrapper("Closed today", android.R.color.holo_red_dark, Typeface.BOLD);
//        new PlaceHourWrapper("Closing soon", android.R.color.holo_red_dark, Typeface.BOLD);
//        new PlaceHourWrapper("Open until " + closeTime, R.color.lime_dark, Typeface.ITALIC);
//        new PlaceHourWrapper("Closed until " + openTime, android.R.color.holo_red_dark, Typeface.BOLD);
    }
}
