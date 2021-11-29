package com.neige_i.go4lunch.data.google_places;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class DetailsRepository extends PlacesRepository<RawDetailsResponse, RestaurantDetails> {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final PlacesApi placesApi;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    DetailsRepository(@NonNull PlacesApi placesApi, @NonNull String mapsApiKey) {
        super(placesApi, mapsApiKey);
        this.placesApi = placesApi;
    }

    // ------------------------------------ REPOSITORY METHODS -------------------------------------

    @NonNull
    @Override
    List<String> toQueryStrings(@NonNull Object... queryParameter) {
        return Collections.singletonList(
            (String) queryParameter[0]
        );
    }

    @NonNull
    @Override
    Call<RawDetailsResponse> getRequest(@NonNull List<String> queryParameters) {
        return placesApi.getRestaurantDetails(queryParameters.get(0));
    }

    @NonNull
    @Override
    String getNameForLog() {
        return "Details";
    }

    @Nullable
    @Override
    RestaurantDetails cleanDataFromRetrofit(@Nullable RawDetailsResponse rawDetailsResponse) {
        if (rawDetailsResponse == null || rawDetailsResponse.getResult() == null) {
            return null;
        }

        final RawDetailsResponse.Result result = rawDetailsResponse.getResult();
        if (result.getPlaceId() == null || result.getBusinessStatus() == null ||
            !result.getBusinessStatus().equals("OPERATIONAL") ||
            result.getGeometry() == null || result.getGeometry().getLocation() == null ||
            result.getGeometry().getLocation().getLat() == null ||
            result.getGeometry().getLocation().getLng() == null ||
            result.getName() == null || result.getFormattedAddress() == null
        ) {
            return null;
        }

        final String photoUrl;
        if (result.getPhotos() == null || result.getPhotos().isEmpty()) {
            photoUrl = null;
        } else {
            photoUrl = getPhotoUrl(result.getPhotos().get(0).getPhotoReference());
        }

        return new RestaurantDetails(
            result.getPlaceId(),
            result.getName(),
            getAddress(result.getFormattedAddress()),
            getRating(result.getRating()),
            photoUrl,
            result.getInternationalPhoneNumber(),
            result.getWebsite(),
            setupOpeningHours(result.getOpeningHours())
        );
    }

    @NonNull
    private List<RestaurantDetails.RestaurantHour> setupOpeningHours(@Nullable RawDetailsResponse.OpeningHours openingHours) {
        if (openingHours == null || openingHours.getPeriods() == null) {
            return Collections.emptyList();
        }
        final List<RestaurantDetails.RestaurantHour> restaurantHourList = new ArrayList<>();

        for (RawDetailsResponse.Period period : openingHours.getPeriods()) {
            final RawDetailsResponse.Open open = period.getOpen();
            final RawDetailsResponse.Close close = period.getClose();

            if (open != null && open.getDay() != null && open.getTime() != null) {
                restaurantHourList.add(new RestaurantDetails.RestaurantHour(
                    true,
                    getDayOfWeek(open.getDay()),
                    getTime(open.getTime())
                ));
            }

            if (close != null && close.getDay() != null && close.getTime() != null) {
                restaurantHourList.add(new RestaurantDetails.RestaurantHour(
                    false,
                    getDayOfWeek(close.getDay()),
                    getTime(close.getTime())
                ));
            }
        }

        return restaurantHourList;
    }

    /**
     * Converts the day from Places API standard to java.time standard.<br />
     * The day starts at Monday=1 for java.time and Sunday=0 for Places API.
     */
    @NonNull
    private DayOfWeek getDayOfWeek(int day) {
        return DayOfWeek.of(day == 0 ? 7 : day);
    }

    @NonNull
    private LocalTime getTime(@NonNull String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
    }
}
