//package com.neige_i.go4lunch.data.google_places.generic;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.neige_i.go4lunch.data.google_places.PlacesApi;
//import com.neige_i.go4lunch.data.google_places.model.RawDetailsResponse;
//import com.neige_i.go4lunch.data.google_places.model.RestaurantDetails;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//import retrofit2.Call;
//
//@Singleton
//public class DetailsRepository extends PlacesRepository<String, RawDetailsResponse, RestaurantDetails> {
//
//    @NonNull
//    private final PlacesApi placesApi;
//
//    @Inject
//    DetailsRepository(@NonNull PlacesApi placesApi) {
//        this.placesApi = placesApi;
//    }
//
//    @NonNull
//    @Override
//    String toStringQuery(@NonNull String query) {
//        return query;
//    }
//
//    @NonNull
//    @Override
//    Call<RawDetailsResponse> getRequest(@NonNull String queryParameter) {
//        return placesApi.getRestaurantDetails(queryParameter);
//    }
//
//    @Nullable
//    @Override
//    RestaurantDetails cleanDataFromRetrofit(@Nullable RawDetailsResponse rawDetailsResponse) {
//        if (rawDetailsResponse == null || rawDetailsResponse.getResult() == null) {
//            return null;
//        }
//
//        final RawDetailsResponse.Result result = rawDetailsResponse.getResult();
//        if (result.getPlaceId() == null || result.getBusinessStatus() == null ||
//            !result.getBusinessStatus().equals("OPERATIONAL") ||
//            result.getGeometry() == null || result.getGeometry().getLocation() == null ||
//            result.getGeometry().getLocation().getLat() == null ||
//            result.getGeometry().getLocation().getLng() == null ||
//            result.getName() == null || result.getFormattedAddress() == null
//        ) {
//            return null;
//        }
//
//        final String photoUrl;
//        if (result.getPhotos() == null || result.getPhotos().isEmpty()) {
//            photoUrl = null;
//        } else {
//            photoUrl = getPhotoUrl(result.getPhotos().get(0).getPhotoReference());
//        }
//
//        return new RestaurantDetails(
//            result.getPlaceId(),
//            result.getName(),
//            result.getFormattedAddress(),
//            getRating(result.getRating()),
//            photoUrl,
//            result.getInternationalPhoneNumber(),
//            result.getWebsite(),
//            getOpeningHours(result.getOpeningHours())
//        );
//
//    }
//
//    @Nullable
//    private List<String> getOpeningHours(@Nullable RawDetailsResponse.OpeningHours openingHours) {
//        if (openingHours == null || openingHours.getPeriods() == null) {
//            return null;
//        } else {
//            final List<String> openingHourList = new ArrayList<>();
//
//            for (RawDetailsResponse.Period period : openingHours.getPeriods()) {
//                final RawDetailsResponse.Open open = period.getOpen();
//                final RawDetailsResponse.Close close = period.getClose();
//
//                if (open != null) {
//                    if (open.getDay() != null && open.getTime() != null) {
//                        openingHourList.add(toJavaDay(open.getDay()) + open.getTime());
//                    } else {
//                        return null;
//                    }
//
//                    if (close == null) {
//                        // When the place is always open
//                        openingHourList.add(null);
//                    } else if (close.getDay() != null && close.getTime() != null) {
//                        openingHourList.add(toJavaDay(close.getDay()) + close.getTime());
//                    } else {
//                        return null;
//                    }
//                }
//            }
//
//            return openingHourList;
//        }
//    }
//
//    /**
//     * Converts a day from Places API standard to java.time standard.<br />
//     * The day starts at monday Monday=1 for java.time and Sunday=0 for Places API;
//     */
//    private int toJavaDay(int placesApiDay) {
//        return placesApiDay == 0 ? 7 : placesApiDay;
//    }
//}
