package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.domain.location.GetGpsStatusUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationUseCase;
import com.neige_i.go4lunch.domain.location.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.place_nearby.GetNearbyRestaurantsUseCase;
import com.neige_i.go4lunch.view.MediatorSingleLiveEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapViewModel extends ViewModel {

    // -------------------------------------- CLASS VARIABLES --------------------------------------

    static final float STREET_ZOOM_LEVEL = 15; // 1=world, 5=continent, 10=city, 15=streets, 20=buildings

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final RequestGpsUseCase requestGpsUseCase;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<MapViewState> mapViewState = new MediatorLiveData<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Flag to make the map camera follows the current location only if the map has not been moved by the user.
     * The map camera is automatically moved whenever a new location is available.
     * But if the user scrolls manually the map, it should stay still even if the location is updated.
     */
    private boolean hasCameraMovedManually;

    private final MutableLiveData<CameraPosition> cameraPositionMutableLiveData = new MutableLiveData<>();

    // MEDIATOR 2
    private final MutableLiveData<Boolean> onCenterMapButtonClickedPing = new MutableLiveData<>();

    @NonNull
    private final MediatorSingleLiveEvent<CameraPosition> cameraPositionMediatorSingleLiveEvent = new MediatorSingleLiveEvent<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public MapViewModel(@NonNull GetLocationPermissionUseCase getLocationPermissionUseCase,
                        @NonNull GetLocationUseCase getLocationUseCase,
                        @NonNull GetNearbyRestaurantsUseCase getNearbyRestaurantsUseCase,
                        @NonNull GetGpsStatusUseCase getGpsStatusUseCase,
                        @NonNull RequestGpsUseCase requestGpsUseCase
    ) {
        this.requestGpsUseCase = requestGpsUseCase;

        final LiveData<Boolean> isLocationPermissionGrantedLiveData = getLocationPermissionUseCase.isGranted();
        final LiveData<Location> locationLiveData = getLocationUseCase.get();
        final LiveData<List<NearbyRestaurant>> nearbyRestaurantsLiveData = getNearbyRestaurantsUseCase.get();
        final LiveData<Boolean> isGpsEnabledLiveData = getGpsStatusUseCase.isEnabled();

        mapViewState.addSource(isLocationPermissionGrantedLiveData, isLocationPermissionGranted ->
            mapCombine(isLocationPermissionGranted, locationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), isGpsEnabledLiveData.getValue(), cameraPositionMutableLiveData.getValue())
        );
        mapViewState.addSource(locationLiveData, location ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), location, nearbyRestaurantsLiveData.getValue(), isGpsEnabledLiveData.getValue(), cameraPositionMutableLiveData.getValue())
        );
        mapViewState.addSource(nearbyRestaurantsLiveData, nearbyRestaurants ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), locationLiveData.getValue(), nearbyRestaurants, isGpsEnabledLiveData.getValue(), cameraPositionMutableLiveData.getValue())
        );
        mapViewState.addSource(isGpsEnabledLiveData, isGpsEnabled ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), locationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), isGpsEnabled, cameraPositionMutableLiveData.getValue())
        );
        mapViewState.addSource(cameraPositionMutableLiveData, cameraPosition ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), locationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), isGpsEnabledLiveData.getValue(), cameraPosition)
        );

        // MEDIATOR 2
        cameraPositionMediatorSingleLiveEvent.addSource(locationLiveData, location -> combineCameraEvent(location, onCenterMapButtonClickedPing.getValue()));
        cameraPositionMediatorSingleLiveEvent.addSource(onCenterMapButtonClickedPing, ping -> combineCameraEvent(locationLiveData.getValue(), ping));
    }

    private void combineCameraEvent(@Nullable Location location, @Nullable Boolean ping) {
        if (location == null || ping == null) {
            return;
        }

        onCenterMapButtonClickedPing.setValue(null);

        cameraPositionMediatorSingleLiveEvent.setValue(
            CameraPosition.fromLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()),
                15
            )
        );

    }

    private void mapCombine(@Nullable Boolean isLocationPermissionGranted,
                            @Nullable Location location,
                            @Nullable List<NearbyRestaurant> nearbyRestaurants,
                            @Nullable Boolean isGpsEnabled,
                            @Nullable CameraPosition cameraPosition
    ) {
        final boolean isLocationGranted = Objects.equals(isLocationPermissionGranted, true);
        final boolean isGpsCurrentlyEnabled = Objects.equals(isGpsEnabled, true);
        final float currentZoom = cameraPosition != null ? cameraPosition.zoom : STREET_ZOOM_LEVEL;

        if (location == null || cameraPosition == null) {
            return;
        }

        final List<MarkerViewState> markers = setupMarkers(nearbyRestaurants);

        // Setup map coordinates (weather or not the map camera should be moved)
        final double latitude;
        final double longitude;
        final float zoom;
        if (!hasCameraMovedManually) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            zoom = Math.max(STREET_ZOOM_LEVEL, currentZoom);
        } else {
            latitude = cameraPosition.target.latitude;
            longitude = cameraPosition.target.longitude;
            zoom = currentZoom;
        }

        // Setup FAB style
        @ColorRes
        final int fabColor = setupFabStyle(location, cameraPosition);

        // Set view state
        System.out.println("before setViewState: " + latitude + ", " + longitude + ", " + zoom);
        mapViewState.setValue(new MapViewState(
            isLocationGranted && isGpsCurrentlyEnabled,
            isLocationGranted,
            R.drawable.ic_gps_off,
            fabColor,
            markers,
            latitude,
            longitude,
            zoom
        ));

        // Setup markers to add
        if (!isGpsCurrentlyEnabled) {
            requestGpsUseCase.request();
        }
    }

    @ColorRes
    private int setupFabStyle(
        @NonNull Location currentLocation,
        @NonNull CameraPosition cameraPosition
    ) {
        // Better use BigDecimal to compare coordinates rather than double values
        final BigDecimal locationLat = toBigDecimal(currentLocation.getLatitude());
        final BigDecimal locationLng = toBigDecimal(currentLocation.getLongitude());
        final BigDecimal mapCameraLat = toBigDecimal(cameraPosition.target.latitude);
        final BigDecimal mapCameraLng = toBigDecimal(cameraPosition.target.longitude);

        if (locationLat.compareTo(mapCameraLat) == 0 && locationLng.compareTo(mapCameraLng) == 0) {
            return R.color.blue_google;
        } else {
            return R.color.black;
        }
    }

    @Nullable
    private List<MarkerViewState> setupMarkers(@Nullable List<NearbyRestaurant> nearbyRestaurants) {
        if (nearbyRestaurants == null) {
            return null;
        }

        final List<MarkerViewState> markerViewStates = new ArrayList<>();

        for (NearbyRestaurant nearbyRestaurant : nearbyRestaurants) {
            markerViewStates.add(new MarkerViewState(
                nearbyRestaurant.getPlaceId(),
                nearbyRestaurant.getName(),
                nearbyRestaurant.getLatitude(),
                nearbyRestaurant.getLongitude(),
                nearbyRestaurant.getAddress()
            ));
        }

        return markerViewStates;
    }

    @NonNull
    public LiveData<MapViewState> getMapViewState() {
        return mapViewState;
    }

    // ---------------------------------------- MAP METHODS ----------------------------------------

    public void onCameraMoved() {
        hasCameraMovedManually = true;
    }

    public void onCameraStopped(@NonNull CameraPosition newCameraPosition) {
        cameraPositionMutableLiveData.setValue(newCameraPosition); // This event is a source that triggers the MapViewState's update
    }

    public void onLocationButtonClicked() {
        hasCameraMovedManually = false;
        onCenterMapButtonClickedPing.setValue(true);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private BigDecimal toBigDecimal(double coordinate) {
        return BigDecimal.valueOf(coordinate).setScale(4, RoundingMode.HALF_UP);
    }
}
