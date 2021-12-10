package com.neige_i.go4lunch.domain.list_restaurant;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.google_places.AutocompleteRepository;
import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.AutocompleteRestaurant;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
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
    @NonNull
    private final Location restaurantLocation;

    // ------------------------------------ LIVE DATA TO EXPOSE ------------------------------------

    @NonNull
    private final MediatorLiveData<List<NearbyDetail>> nearbyDetailList = new MediatorLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    @NonNull
    private final MediatorLiveData<Map<String, RestaurantDetails>> restaurantDetailsMediatorLiveData = new MediatorLiveData<>();
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
        @NonNull AutocompleteRepository autocompleteRepository,
        @NonNull Clock clock,
        @NonNull Location restaurantLocation
    ) {
        this.detailsRepository = detailsRepository;
        this.firestoreRepository = firestoreRepository;
        this.clock = clock;
        this.restaurantLocation = restaurantLocation;

        restaurantDetailsMediatorLiveData.setValue(new HashMap<>());
        interestedWorkmatesMediatorLiveData.setValue(new HashMap<>());

        final LiveData<Location> currentLocationLiveData = locationRepository.getCurrentLocation();
        final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData = Transformations.switchMap(
            currentLocationLiveData, location -> nearbyRepository.getData(location)
        );
        final LiveData<AutocompleteRestaurant> searchQueryLiveData = autocompleteRepository.getCurrentSearchQuery();

        nearbyDetailList.addSource(currentLocationLiveData, location -> combine(location, nearbyRestaurantsLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        nearbyDetailList.addSource(nearbyRestaurantsLiveData, nearbyRestaurants -> combine(currentLocationLiveData.getValue(), nearbyRestaurants, restaurantDetailsMediatorLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        nearbyDetailList.addSource(restaurantDetailsMediatorLiveData, restaurantDetailsMap -> combine(currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), restaurantDetailsMap, interestedWorkmatesMediatorLiveData.getValue(), searchQueryLiveData.getValue()));
        nearbyDetailList.addSource(interestedWorkmatesMediatorLiveData, interestedWorkmatesMap -> combine(currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), interestedWorkmatesMap, searchQueryLiveData.getValue()));
        nearbyDetailList.addSource(searchQueryLiveData, searchQuery -> combine(currentLocationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), restaurantDetailsMediatorLiveData.getValue(), interestedWorkmatesMediatorLiveData.getValue(), searchQuery));
    }

    private void combine(
        @Nullable Location currentLocation,
        @Nullable List<NearbyRestaurant> nearbyRestaurants,
        @Nullable Map<String, RestaurantDetails> restaurantDetailsMap,
        @Nullable Map<String, Integer> interestedWorkmatesMap,
        @Nullable AutocompleteRestaurant searchQuery
    ) {
        if (currentLocation == null || nearbyRestaurants == null) {
            return;
        }

        if (restaurantDetailsMap == null || interestedWorkmatesMap == null) {
            throw new IllegalStateException("Impossible state: maps are initialized in the constructor!");
        }

        final List<NearbyDetail> nearbyDetails = new ArrayList<>();

        for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
            final String restaurantId = nearbyRestaurant.getPlaceId();

            if (!queriedRestaurants.contains(restaurantId)) {
                queriedRestaurants.add(restaurantId);

                // Query restaurants details
                restaurantDetailsMediatorLiveData.addSource(
                    detailsRepository.getData(restaurantId), restaurantDetails -> {

                        restaurantDetailsMap.put(restaurantId, restaurantDetails);
                        restaurantDetailsMediatorLiveData.setValue(restaurantDetailsMap);
                    }
                );

                // Query interested workmates count
                interestedWorkmatesMediatorLiveData.addSource(
                    firestoreRepository.getWorkmatesEatingAt(restaurantId), users -> {

                        interestedWorkmatesMap.put(restaurantId, users.size());
                        interestedWorkmatesMediatorLiveData.setValue(interestedWorkmatesMap);
                    }
                );
            }

            final RestaurantDetails restaurantDetails = restaurantDetailsMap.get(restaurantId);
            final String restaurantName = nearbyRestaurant.getName();

            if (searchQuery == null ||
                restaurantName.toLowerCase().contains(searchQuery.getRestaurantName().toLowerCase()) ||
                restaurantId.equals(searchQuery.getPlaceId())
            ) {
                // Setup restaurant latitude & longitude
                restaurantLocation.setLatitude(nearbyRestaurant.getLatitude());
                restaurantLocation.setLongitude(nearbyRestaurant.getLongitude());

                final Integer interestedWorkmatesCount = interestedWorkmatesMap.get(restaurantId);

                nearbyDetails.add(new NearbyDetail(
                    restaurantId,
                    restaurantName,
                    nearbyRestaurant.getAddress(),
                    currentLocation.distanceTo(restaurantLocation),
                    getHourResult(restaurantDetails != null ? restaurantDetails.getOpeningPeriods() : null),
                    interestedWorkmatesCount != null ? interestedWorkmatesCount : 0,
                    nearbyRestaurant.getRating(),
                    nearbyRestaurant.getPhotoUrl()
                ));
            }
        }

        nearbyDetailList.setValue(nearbyDetails);
    }

    @NonNull
    private HourResult getHourResult(@Nullable List<RestaurantDetails.RestaurantHour> restaurantHours) {
        if (restaurantHours == null) {
            return new HourResult.Loading();
        } else if (restaurantHours.isEmpty()) {
            return new HourResult.Unknown();
        }

        final RestaurantDetails.RestaurantHour alwaysOpenHour = new RestaurantDetails.RestaurantHour(true, DayOfWeek.SUNDAY, LocalTime.of(0, 0));
        if (restaurantHours.size() == 1 && restaurantHours.get(0).equals(alwaysOpenHour)) {
            return new HourResult.AlwaysOpen();
        }

        final LocalDateTime now = LocalDateTime.now(clock);

        // Get RestaurantHour's DateTime and sort by it (in ascending order)
        Collections.sort(restaurantHours, Comparator.comparing(restaurantHour -> {
            return getRestaurantDateTime(now, restaurantHour);
        }));

        for (RestaurantDetails.RestaurantHour restaurantHour : restaurantHours) {
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

        // Should never happen
        throw new IllegalStateException(
            "The " + RestaurantDetails.RestaurantHour.class.getSimpleName() + "'s list is sorted according to the" +
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
        @NonNull RestaurantDetails.RestaurantHour restaurantHour
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
