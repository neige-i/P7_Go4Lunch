package com.neige_i.go4lunch.view.list;

import android.graphics.Typeface;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.LocationRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.view.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final NearbyRepository nearbyRepository;

    public ListViewModel(LocationRepository locationRepository, NearbyRepository nearbyRepository) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = nearbyRepository;
    }

    public LiveData<List<RestaurantViewState>> getViewState() {
        return Transformations.switchMap(
            locationRepository.getCurrentLocation(),
            userLocation -> Transformations.map(
                nearbyRepository.getPlacesResponse(userLocation),
                nearbyResponse -> {
                    final List<RestaurantViewState> viewStates = new ArrayList<>();

                    if (nearbyResponse != null) {
                        final List<NearbyResponse.Result> resultList = ((NearbyResponse) nearbyResponse).getResults();
                        if (resultList != null) {
                            for (NearbyResponse.Result result : resultList) {

                                final NearbyResponse.Location restaurantLocation = result.getGeometry().getLocation();
                                final float[] distances = new float[3];
                                Location.distanceBetween(
                                    userLocation.getLatitude(),
                                    userLocation.getLongitude(),
                                    restaurantLocation.getLat(),
                                    restaurantLocation.getLng(),
                                    distances
                                );

                                // TODO: handle opening hours

                                viewStates.add(new RestaurantViewState(
                                    result.getPlaceId(),
                                    result.getName(),
                                    distances[0],
                                    Util.getFormattedDistance(distances[0]),
                                    Util.getShortAddress(result.getVicinity()),
                                    Typeface.BOLD_ITALIC,
                                    R.color.lime,
                                    "Open",
                                    true,
                                    2,
                                    Util.getRating(result.getRating()),
                                    Util.getPhotoUrl(result.getPhotos())
                                ));

                                Collections.sort(
                                    viewStates,
                                    (viewState1, viewState2) -> (int) (viewState1.getDistance() - viewState2.getDistance())
                                );
                            }
                        }
                    }

                    return viewStates;
                }
            )
        );
    }
}
