package com.neige_i.go4lunch.view.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.neige_i.go4lunch.data.google_places.LocationRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final NearbyRepository nearbyRepository;

    @NonNull
    private final MediatorLiveData<MapViewState> viewState = new MediatorLiveData<>();

    private boolean isLocationPermissionGranted;
    private double mapLatitude;
    private double mapLongitude;
    private float mapZoom = ZOOM_LEVEL_STREETS;
    private static final float ZOOM_LEVEL_STREETS = 15f; // Zoom levels: 1-world, 5-continent, 10-city, 15-streets, 20-buildings

    public MapViewModel(@NonNull NearbyRepository nearbyRepository, @NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = nearbyRepository;
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
    public void onMapAvailable(double mapLat, double mapLng) {
        // Init map location
        mapLatitude = mapLat;
        mapLongitude = mapLng;

        final LiveData<Boolean> isLocationPermissionGranted = locationRepository.isLocationPermissionGranted();
        final LiveData<NearbyResponse> nearbyResponse = Transformations.switchMap(
            locationRepository.getCurrentLocation(),
            location -> {
                // Update map location
                mapLatitude = location.getLatitude();
                mapLongitude = location.getLongitude();

                return Transformations.map(
                    nearbyRepository.getPlacesResponse(location),
                    response -> (NearbyResponse) response
                );
            }
        );

        viewState.addSource(
            isLocationPermissionGranted,
            isPermissionGranted -> combine(isPermissionGranted, nearbyResponse.getValue())
        );
        viewState.addSource(
            nearbyResponse,
            nearbyResponseValue -> combine(isLocationPermissionGranted.getValue(), nearbyResponseValue)
        );
    }

    public void onCameraCentered(float currentMapZoom) {
        if (isLocationPermissionGranted) {
            // Update current map zoom if it is higher from the ground that 'streets' level
            mapZoom = Math.max(currentMapZoom, ZOOM_LEVEL_STREETS);

            final MapViewState currentViewState = viewState.getValue(); // ASKME: check nullability
            viewState.setValue(new MapViewState(
                currentViewState.isLocationLayerEnabled(),
                currentViewState.getMarkerViewStates(),
                mapLatitude,
                mapLongitude,
                mapZoom
            ));
        }
    }

    private void combine(@Nullable Boolean isPermissionEnabled, @Nullable NearbyResponse nearbyResponse) {
        // ASKME: remove nearbyResponse from condition
        if (isPermissionEnabled == null || nearbyResponse == null)
            return;

        isLocationPermissionGranted = isPermissionEnabled;

        final List<MarkerViewState> markerViewStates = new ArrayList<>();
        if (nearbyResponse != null) {
            final List<NearbyResponse.Result> resultList = nearbyResponse.getResults();
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

        viewState.setValue(new MapViewState(
            isPermissionEnabled,
            markerViewStates,
            mapLatitude,
            mapLongitude,
            mapZoom
        ));
    }
}
