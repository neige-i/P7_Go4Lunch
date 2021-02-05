package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.neige_i.go4lunch.data.google_places.BaseRepository;
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

    private final MediatorLiveData<MapViewState> viewState = new MediatorLiveData<>();

    private boolean isLocationPermissionGranted;
    @Nullable
    private Location currentLocation;
    @Nullable
    private CameraUpdate mapCamera;
    private static final float ZOOM_LEVEL_STREETS = 15f; // Zoom levels: 1-world, 5-continent, 10-city, 15-streets, 20-buildings

    public MapViewModel(@NonNull BaseRepository nearbyRepository, @NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = (NearbyRepository) nearbyRepository; // TODO: change this cast to ViewModelFactory
    }

    public LiveData<MapViewState> getViewState() {
        return viewState;
    }

    public void onMapAvailable(CameraPosition cameraPosition) {
        mapCamera = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);

        final LiveData<Boolean> isLocationPermissionGranted = locationRepository.isLocationPermissionGranted();
        final LiveData<NearbyResponse> nearbyResponse = Transformations.switchMap(
            locationRepository.getCurrentLocation(),
            location -> {
                currentLocation = location;
                return Transformations.map(
                    nearbyRepository.executeDetailsRequest(location),
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
        if (isLocationPermissionGranted && currentLocation != null) {
            mapCamera = CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                Math.max(currentMapZoom, ZOOM_LEVEL_STREETS) // Zoom to 'streets' level if current zoom is higher
            );

            final MapViewState currentViewState = viewState.getValue(); // ASKME: check nullability
            viewState.setValue(new MapViewState(
                currentViewState.isLocationLayerEnabled(),
                currentViewState.getMarkerViewStates(),
                mapCamera
            ));
        }
    }

    private void combine(@Nullable Boolean isPermissionEnabled, @Nullable NearbyResponse nearbyResponse) {
        if (isPermissionEnabled == null)
            return;

        isLocationPermissionGranted = isPermissionEnabled;

        final List<MarkerViewState> markerViewStates = new ArrayList<>();
        if (nearbyResponse != null) {
            final List<NearbyResponse.Result> resultList = ((NearbyResponse) nearbyResponse).getResults();
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
            mapCamera
        ));
    }
}
