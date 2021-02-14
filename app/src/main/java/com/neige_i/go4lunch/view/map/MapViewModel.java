package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.domain.GetNearbyRestaurantsUseCase;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    // Zoom levels: 1-world, 5-continent, 10-city, 15-streets, 20-buildings
    static final float ZOOM_LEVEL_STREETS = 15f;

    @NonNull
    private final GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase;

    @NonNull
    private final MediatorLiveData<MapViewState> viewState = new MediatorLiveData<>();

    private boolean isLocationPermissionGranted;
    @Nullable
    private Location currentLocation;
    @NonNull
    private final List<MarkerViewState> markerViewStates = new ArrayList<>();
    private double mapLatitude;
    private double mapLongitude;
    private float mapZoom;

    public MapViewModel(@NonNull GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase) {
        this.getNearbyRestaurantsUseCase = getNearbyRestaurantsUseCase;
    }

    @NonNull
    public LiveData<MapViewState> getViewState() {
        return viewState;
    }

    /**
     * {@link MapViewState} holds data that will be used by a {@link com.google.android.gms.maps.GoogleMap GoogleMap} object.<br />
     * This method must be called inside
     * {@link com.google.android.gms.maps.SupportMapFragment#getMapAsync(OnMapReadyCallback) SupportMapFragment.getMapAsync()}
     * when the {@link com.google.android.gms.maps.GoogleMap GoogleMap} object is ready to be used.
     */
    public void onMapAvailable(double mapLat, double mapLng, float zoom) {
        // Init camera position
        mapLatitude = mapLat;
        mapLongitude = mapLng;
        mapZoom = zoom;

        viewState.addSource(getNearbyRestaurantsUseCase.getNearby(), mapModel -> {
            // Update markers
            markerViewStates.clear();
            if (mapModel.getNearbyResponse() != null) {
                final List<NearbyResponse.Result> resultList = mapModel.getNearbyResponse().getResults();
                if (resultList != null) {
                    for (NearbyResponse.Result result : resultList) {
                        final NearbyResponse.Location nearbyLocation = result.getGeometry().getLocation();
                        markerViewStates.add(new MarkerViewState(
                            result.getPlaceId(),
                            result.getName(),
                            nearbyLocation.getLat(),
                            nearbyLocation.getLng(),
                            result.getVicinity()
                        ));
                    }
                }
            }

            isLocationPermissionGranted = mapModel.isLocationPermissionGranted();
            currentLocation = mapModel.getCurrentLocation();

            setViewState();
        });
    }

    public void onCameraIdled(double mapLat, double mapLng, float zoom) {
        // Update camera position
        mapLatitude = mapLat;
        mapLongitude = mapLng;
        mapZoom = zoom;
    }

    public void onCameraCentered() {
        setViewState();
    }

    private void setViewState() {
        // Update camera position if current position is available
        if (currentLocation != null) {
            mapLatitude = currentLocation.getLatitude();
            mapLongitude = currentLocation.getLongitude();
            mapZoom = Math.max(mapZoom, ZOOM_LEVEL_STREETS);
        }

        // Update view state value
        viewState.setValue(new MapViewState(
            isLocationPermissionGranted,
            markerViewStates,
            mapLatitude,
            mapLongitude,
            mapZoom
        ));
    }
}
