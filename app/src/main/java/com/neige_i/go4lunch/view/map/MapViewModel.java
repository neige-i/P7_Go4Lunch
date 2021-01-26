package com.neige_i.go4lunch.view.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.google_places.PlacesRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private final MediatorLiveData<List<MapViewState>> mapViewStateMediatorLiveData = new MediatorLiveData<>();

    private final MediatorLiveData<Boolean> isLocationLayerEnabled = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> isMapAvailable = new MutableLiveData<>();

    public MapViewModel(@NonNull PlacesRepository placesRepository) {
        mapViewStateMediatorLiveData.addSource(placesRepository.getNearbyRestaurants(), this::combine);

        isMapAvailable.setValue(false);
        isLocationLayerEnabled.addSource(
            placesRepository.isLocationPermissionGranted(),
            isPermissionGranted -> combineGoogleMap(isMapAvailable.getValue(), isPermissionGranted)
        );
        isLocationLayerEnabled.addSource(
            isMapAvailable,
            mapAvailable -> combineGoogleMap(mapAvailable, placesRepository.isLocationPermissionGranted().getValue())
        );
    }

    public LiveData<List<MapViewState>> getMapViewStateLiveData() {
        return mapViewStateMediatorLiveData;
    }

    public LiveData<Boolean> isLocationLayerEnabled() {
        return isLocationLayerEnabled;
    }

    public void onMapAvailable() {
        isMapAvailable.setValue(true);
    }

    private void combineGoogleMap(boolean isMapAvailable, boolean isPermissionEnabled) {
        if (isMapAvailable)
            isLocationLayerEnabled.setValue(isPermissionEnabled);
    }

    private void combine(@Nullable NearbyResponse nearbyResponse) {
        if (nearbyResponse != null) {
            map(nearbyResponse);
        }
    }

    private void map(@NonNull NearbyResponse nearbyResponse) {
        if (nearbyResponse.getResults() != null) {
            List<MapViewState> viewStates = new ArrayList<>();

            for (NearbyResponse.Result result : nearbyResponse.getResults()) {
                viewStates.add(
                    new MapViewState(
                        result.getPlaceId(),
                        result.getName()
                    )
                );
            }

            mapViewStateMediatorLiveData.setValue(viewStates);
        }
    }
}
