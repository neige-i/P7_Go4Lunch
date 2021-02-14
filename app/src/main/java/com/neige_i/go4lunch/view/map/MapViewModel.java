package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    // Zoom levels: 1-world, 5-continent, 10-city, 15-streets, 20-buildings
    static final float ZOOM_LEVEL_STREETS = 15f;

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final NearbyRepository nearbyRepository;

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
    public void onMapAvailable(double mapLat, double mapLng, float zoom) {
        // Init camera position
        mapLatitude = mapLat;
        mapLongitude = mapLng;
        mapZoom = zoom;

        final LiveData<Boolean> locationPermissionLiveData = locationRepository.getLocationPermission();
        final LiveData<Location> locationLiveData = locationRepository.getCurrentLocation();

        // ASKME: (for tests) logic in repo: no permission -> no location -> no nearby response
        final LiveData<NearbyResponse> nearbyResponse = /*Transformations.switchMap(
            locationPermissionLiveData,
            isPermissionGranted -> {
                if (isPermissionGranted) {
                    return*/ Transformations.switchMap(
                        locationLiveData,
                        location -> {
                            // Update current location
                            currentLocation = location;

                            return Transformations.map(
                                nearbyRepository.getNearbyResponse(location),
                                response -> response
                            );
                        }
                    );
//                } else {
//                    return new MutableLiveData<>();
//                }
//            }
//        );

        // ASKME: without 2 awaits in test class, add sources in the reverse order will fire an AssertionError
        //  because combine() update view state's LiveData value if response is null or not, but only if permission in not null
        viewState.addSource(
            nearbyResponse,
            nearbyResponseValue -> combine(locationPermissionLiveData.getValue(), nearbyResponseValue)
        );
        viewState.addSource(
            locationPermissionLiveData,
            isPermissionGranted -> combine(isPermissionGranted, nearbyResponse.getValue())
        );
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

    private void combine(@Nullable Boolean isPermissionEnabled, @Nullable NearbyResponse nearbyResponse) {
        if (isPermissionEnabled == null)
            return;

        // Update location permission
        isLocationPermissionGranted = isPermissionEnabled;

        // Update markers
        markerViewStates.clear();
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

        setViewState();
    }

    private void setViewState() {
        // Update camera position if current position is available
        if (isLocationPermissionGranted && currentLocation != null) {
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
