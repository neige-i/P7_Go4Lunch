package com.neige_i.go4lunch.view.list;

import android.graphics.Typeface;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.BuildConfig;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.PlacesRepository;
import com.neige_i.go4lunch.data.google_places.LocationRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final NearbyRepository nearbyRepository;

    public ListViewModel(LocationRepository locationRepository, PlacesRepository nearbyRepository) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = (NearbyRepository) nearbyRepository;
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

                                final List<NearbyResponse.Photo> photoList = result.getPhotos();
                                final String photoUrl;
                                if (photoList != null && !photoList.isEmpty())
                                    photoUrl = photoList.get(0).getPhotoReference();
                                else
                                    photoUrl = "";

                                viewStates.add(new RestaurantViewState(
                                    result.getPlaceId(),
                                    result.getName(),
                                    (int) distances[0],
                                    result.getVicinity(),
                                    Typeface.BOLD_ITALIC,
                                    R.color.lime,
                                    "Open",
                                    true,
                                    2,
                                    2,
                                    "https://maps.googleapis.com/maps/api/place/photo?maxheight=1080&key=" + BuildConfig.MAPS_API_KEY +
                                        "&photoreference=" + photoUrl
                                ));

                                Collections.sort(
                                    viewStates,
                                    (viewState1, viewState2) -> viewState1.getDistance() - viewState2.getDistance()
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
