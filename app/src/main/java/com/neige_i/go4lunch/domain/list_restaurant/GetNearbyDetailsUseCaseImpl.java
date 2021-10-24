package com.neige_i.go4lunch.domain.list_restaurant;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RestaurantHour;
import com.neige_i.go4lunch.data.location.LocationRepository;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class GetNearbyDetailsUseCaseImpl implements GetNearbyDetailsUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final DetailsRepository detailsRepository;
    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final Clock clock;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MediatorLiveData<List<NearbyDetail>> nearbyDetailList = new MediatorLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final MediatorLiveData<Map<String, List<RestaurantHour>>> restaurantHoursMediatorLiveData = new MediatorLiveData<>();
    @NonNull
    private final MediatorLiveData<Map<String, Integer>> interestedWorkmatesMediatorLiveData = new MediatorLiveData<>();

    @NonNull
    private final List<String> queriedRestaurants = new ArrayList<>();

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    GetNearbyDetailsUseCaseImpl(
        @NonNull LocationRepository locationRepository,
        @NonNull NearbyRepository nearbyRepository,
        @NonNull DetailsRepository detailsRepository,
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull Clock clock
    ) {
        this.detailsRepository = detailsRepository;
        this.firestoreRepository = firestoreRepository;
        this.clock = clock;

        restaurantHoursMediatorLiveData.setValue(new HashMap<>());
        interestedWorkmatesMediatorLiveData.setValue(new HashMap<>());

        final LiveData<Location> currentLocationLiveData = locationRepository.getCurrentLocation();
        final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData = Transformations.switchMap(
            currentLocationLiveData, location -> nearbyRepository.getNearbyRestaurants(location)
        );

        nearbyDetailList.addSource(currentLocationLiveData, location -> combine(location, nearbyRestaurantsLiveData.getValue(), restaurantHoursMediatorLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue()));
        nearbyDetailList.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> combine(currentLocationLiveData.getValue(), nearbyRestaurants, restaurantHoursMediatorLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue()));
        nearbyDetailList.addSource(restaurantHoursMediatorLiveData, openingHours -> combine(currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), openingHours, interestedWorkmatesMediatorLiveData.getValue()));
        nearbyDetailList.addSource(interestedWorkmatesMediatorLiveData, interestedWorkmates -> combine(currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), restaurantHoursMediatorLiveData.getValue(), interestedWorkmates));
    }

    private void combine(
        @Nullable Location currentLocation,
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @Nullable Map<String, List<RestaurantHour>> restaurantHours,
        @Nullable Map<String, Integer> interestedWorkmates
    ) {
        if (currentLocation == null || nearbyRestaurants == null) {
            return;
        }

        if (restaurantHours == null || interestedWorkmates == null) {
            throw new IllegalStateException("Impossible state: maps are initialized in the constructor!");
        }

        final List<NearbyDetail> nearbyDetails = new ArrayList<>();

        for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
            final String restaurantId = nearbyRestaurant.getPlaceId();

            if (!queriedRestaurants.contains(restaurantId)) {
                queriedRestaurants.add(restaurantId);

                // Query opening hours
                restaurantHoursMediatorLiveData.addSource(
                    detailsRepository.getRestaurantDetails(restaurantId), restaurantDetails -> {

                        restaurantHours.put(restaurantId, restaurantDetails.getOpeningPeriods());
                        restaurantHoursMediatorLiveData.setValue(restaurantHours);
                    }
                );

                // Query interested workmates count
                interestedWorkmatesMediatorLiveData.addSource(
                    firestoreRepository.getWorkmatesEatingAt(restaurantId), users -> {

                        interestedWorkmates.put(restaurantId, users.size());
                        interestedWorkmatesMediatorLiveData.setValue(interestedWorkmates);
                    }
                );
            }

            // Compute the distance to the current location
            final float[] distances = new float[3];
            Location.distanceBetween(
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                nearbyRestaurant.getLatitude(),
                nearbyRestaurant.getLongitude(),
                distances
            );
            final float distanceInMeters = distances[0];

            final Integer interestedWorkmatesCount = interestedWorkmates.get(restaurantId);

            nearbyDetails.add(new NearbyDetail(
                restaurantId,
                nearbyRestaurant.getName(),
                nearbyRestaurant.getAddress(),
                distanceInMeters,
                nearbyRestaurant.getRating(),
                nearbyRestaurant.getPhotoUrl(),
                getHourResult(restaurantHours.get(restaurantId)),
                interestedWorkmatesCount != null ? interestedWorkmatesCount : 0
            ));
        }

        nearbyDetailList.setValue(nearbyDetails);
    }

    @NonNull
    private HourResult getHourResult(@Nullable List<RestaurantHour> restaurantHours) {
        if (restaurantHours == null) {
            return new HourResult.Loading();
        } else if (restaurantHours.isEmpty()) {
            return new HourResult.Unknown();
        }

        final RestaurantHour alwaysOpenHour = new RestaurantHour(true, DayOfWeek.SUNDAY, LocalTime.of(0, 0));
        if (restaurantHours.size() == 1 && restaurantHours.get(0).equals(alwaysOpenHour)) {
            return new HourResult.AlwaysOpen();
        }

        final LocalDateTime now = LocalDateTime.now(clock);

        // Get RestaurantHour's DateTime and sort by it (in ascending order)
        Collections.sort(restaurantHours, Comparator.comparing(restaurantHour -> {
            return getRestaurantDateTime(now, restaurantHour);
        }));

        for (RestaurantHour restaurantHour : restaurantHours) {
            final LocalDateTime restaurantDateTime = getRestaurantDateTime(now, restaurantHour);

            if (now.isBefore(restaurantDateTime)) {
                // Ignore the time part while counting the number of days between the two dates
                final int dayDiff = (int) ChronoUnit.DAYS.between(
                    now.truncatedTo(ChronoUnit.DAYS),
                    restaurantDateTime.truncatedTo(ChronoUnit.DAYS)
                );

                if (restaurantHour.isOpen()) {
                    return new HourResult.Closed(dayDiff, restaurantDateTime);
                } else if (ChronoUnit.HOURS.between(now, restaurantDateTime) < 1) {
                    return new HourResult.ClosingSoon();
                } else {
                    return new HourResult.Open(dayDiff, restaurantDateTime);
                }
            }
        }

        throw new IllegalStateException(
            "The " + RestaurantHour.class.getSimpleName() + "'s list is sorted according to the" +
                "same DateTime that is used in the condition 'isBefore()' inside the for loop." +
                "This way, the condition must be true at least once while iterating through the list.\n" +
                "BUT CURRENTLY: DateTime='" + now + "', list='" + restaurantHours + "'."
        );
    }

    /**
     * Returns the restaurant's DateTime which is based on the one passed in parameter and
     * adjusted with the restaurant's properties.
     */
    @NonNull
    private LocalDateTime getRestaurantDateTime(
        @NonNull LocalDateTime localDateTime,
        @NonNull RestaurantHour restaurantHour
    ) {
        return localDateTime
            .with(TemporalAdjusters.nextOrSame(restaurantHour.getDayOfWeek()))
            .with(restaurantHour.getLocalTime());
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<List<NearbyDetail>> get() {
        return nearbyDetailList;
    }
}
