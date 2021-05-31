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

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    static final float ZOOM_LEVEL_STREETS = 15; // 1=world, 5=continent, 10=city, 15=streets, 20=buildings

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<MapViewState> viewState = new MediatorLiveData<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    @Nullable
    private MapViewState currentViewState;
    @Nullable
    private Location currentLocation;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public MapViewModel(@NonNull GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase) {
        this.getNearbyRestaurantsUseCase = getNearbyRestaurantsUseCase;
    }

    @NonNull
    public LiveData<MapViewState> getViewState() {
        return viewState;
    }

    // ---------------------------------------- MAP METHODS ----------------------------------------

    /**
     * {@link MapViewState} holds data that will be used by a {@link com.google.android.gms.maps.GoogleMap GoogleMap} object.<br />
     * This method must be called inside
     * {@link com.google.android.gms.maps.SupportMapFragment#getMapAsync(OnMapReadyCallback) SupportMapFragment.getMapAsync()}
     * when the {@link com.google.android.gms.maps.GoogleMap GoogleMap} object is ready to be used.
     */
    public void onMapAvailable(double mapLat, double mapLng, float mapZoom) {
        final List<MarkerViewState> markerViewStates = new ArrayList<>();

        viewState.addSource(getNearbyRestaurantsUseCase.getNearby(), mapModel -> {
            // Update markers
            markerViewStates.clear();
            if (mapModel.getNearbyResponse() != null) {
                final List<NearbyResponse.Result> resultList = mapModel.getNearbyResponse().getResults();
                if (resultList != null) {
                    for (NearbyResponse.Result result : resultList) {
                        final NearbyResponse.Location nearbyLocation;
                        if (result.getGeometry() != null) {
                            nearbyLocation = result.getGeometry().getLocation();
                            if (nearbyLocation != null && nearbyLocation.getLat() != null && nearbyLocation.getLng() != null) {
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
                }
            }

            currentLocation = mapModel.getCurrentLocation();
            currentViewState = new MapViewState(mapModel.isLocationPermissionGranted(), markerViewStates, mapLat, mapLng, mapZoom);
            updateCameraPosition();
        });
    }

    /**
     * Updates map's zoom.
     */
    public void onCameraIdled(float zoom) {
        if (currentViewState != null) {
            currentViewState = new MapViewState(
                currentViewState.isLocationLayerEnabled(),
                currentViewState.getMarkerViewStates(),
                currentViewState.getMapLatitude(),
                currentViewState.getMapLongitude(),
                zoom
            );
        }
    }

    public void onCameraCentered() {
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        if (currentLocation != null && currentViewState != null) {
            currentViewState = new MapViewState(
                currentViewState.isLocationLayerEnabled(),
                currentViewState.getMarkerViewStates(),
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                Math.max(currentViewState.getMapZoom(), ZOOM_LEVEL_STREETS)
            );
        }

        // Update view state value
        viewState.setValue(currentViewState);
    }
}
