package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.google_places.BaseRepository;
import com.neige_i.go4lunch.data.google_places.LocationRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;
    private final BaseRepository nearbyRepository;

    private final MediatorLiveData<Boolean> isLocationLayerEnabled = new MediatorLiveData<>();

    private final SingleLiveEvent<Location> zoomMapToCurrentLocationEvent = new SingleLiveEvent<>();

    private final MutableLiveData<Boolean> isMapAvailable = new MutableLiveData<>();
    private final LiveData<Boolean> isLocationPermissionGranted;

    public MapViewModel(@NonNull BaseRepository nearbyRepository, @NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = nearbyRepository;

        isLocationPermissionGranted = locationRepository.isLocationPermissionGranted();
        isLocationLayerEnabled.addSource(
            isLocationPermissionGranted,
            isPermissionGranted -> combineGoogleMap(isMapAvailable.getValue(), isPermissionGranted)
        );
        isLocationLayerEnabled.addSource(
            isMapAvailable,
            mapAvailable -> combineGoogleMap(mapAvailable, isLocationPermissionGranted.getValue())
        );
    }

    public LiveData<List<MapViewState>> getViewState() {
        return Transformations.switchMap(locationRepository.getCurrentLocation(), location ->
            Transformations.map(nearbyRepository.executeDetailsRequest(location), nearbyResponse -> {
                final List<MapViewState> viewStates = new ArrayList<>();

                final List<NearbyResponse.Result> resultList = ((NearbyResponse) nearbyResponse).getResults();
                if (resultList != null) {
                    for (NearbyResponse.Result result : resultList) {
                        final NearbyResponse.Location nearbyLocation = result.getGeometry().getLocation();
                        viewStates.add(new MapViewState(
                            result.getPlaceId(),
                            result.getName(),
                            nearbyLocation.getLat(),
                            nearbyLocation.getLng(),
                            result.getVicinity()
                        ));
                    }
                }
                return viewStates;
            }));
    }

    public LiveData<Boolean> isLocationLayerEnabled() {
        return isLocationLayerEnabled;
    }

    public LiveData<Location> getZoomMapToCurrentLocationEvent() {
        return zoomMapToCurrentLocationEvent;
    }

    public void onMapAvailable() {
        isMapAvailable.setValue(true);
    }

    public void onCurrentLocationQueried() {
        final Location currentLocation = locationRepository.getCurrentLocation().getValue();
        if (isLocationPermissionGranted.getValue() && currentLocation != null) {
            zoomMapToCurrentLocationEvent.setValue(currentLocation);
        }
    }

    private void combineGoogleMap(Boolean isMapAvailable, Boolean isPermissionEnabled) {
        if (isMapAvailable == null || isPermissionEnabled == null)
            return;

        if (isMapAvailable)
            isLocationLayerEnabled.setValue(isPermissionEnabled);
    }
}
