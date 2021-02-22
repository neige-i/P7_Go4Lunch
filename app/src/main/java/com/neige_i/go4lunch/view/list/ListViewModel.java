package com.neige_i.go4lunch.view.list;

import android.graphics.Typeface;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.domain.GetRestaurantDetailsListUseCase;
import com.neige_i.go4lunch.view.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListViewModel extends ViewModel {

    @NonNull
    private final GetRestaurantDetailsListUseCase getRestaurantDetailsListUseCase;

    public ListViewModel(@NonNull GetRestaurantDetailsListUseCase getRestaurantDetailsListUseCase) {
        this.getRestaurantDetailsListUseCase = getRestaurantDetailsListUseCase;
    }

    public LiveData<List<RestaurantViewState>> getViewState() {
        // 1. FETCH the list model
        return Transformations.map(getRestaurantDetailsListUseCase.getDetailsList(), listModel -> {
            // The view state to return
            final List<RestaurantViewState> viewStates = new ArrayList<>();

            if (listModel != null) {

                // 2. ITERATE through the list of details responses
                for (DetailsResponse response : listModel.getDetailsResponses()) {

                    final DetailsResponse.Result result = response.getResult();
                    if (result != null) {

                        // 3. GET the distance between the restaurant and the current location
                        final DetailsResponse.Location restaurantLocation = result.getGeometry().getLocation();
                        final float[] distances = new float[3];
                        Location.distanceBetween(
                            listModel.getCurrentLocation().getLatitude(),
                            listModel.getCurrentLocation().getLongitude(),
                            restaurantLocation.getLat(),
                            restaurantLocation.getLng(),
                            distances
                        );

                        // TODO: handle opening hours

                        // MAPPING
                        viewStates.add(new RestaurantViewState(
                            result.getPlaceId(),
                            result.getName(),
                            distances[0],
                            Util.getFormattedDistance(distances[0]),
                            Util.getShortAddress(result.getFormattedAddress()),
                            Typeface.BOLD_ITALIC,
                            R.color.lime,
                            "Open",
                            true,
                            2,
                            Util.getRating(result.getRating()),
                            Util.getPhotoUrl(result.getPhotos())
                        ));

                        // SORT the restaurant list by distance
                        Collections.sort(
                            viewStates,
                            (viewState1, viewState2) -> (int) (viewState1.getDistance() - viewState2.getDistance())
                        );
                    }
                }
            }

            return viewStates;
        });
    }
}
