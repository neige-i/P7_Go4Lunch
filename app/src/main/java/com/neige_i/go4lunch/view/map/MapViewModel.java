package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.data.google_places.model.NearbyRestaurant;
import com.neige_i.go4lunch.domain.location.GetGpsStatusUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationUseCase;
import com.neige_i.go4lunch.domain.location.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.place_nearby.GetNearbyRestaurantsUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

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
    private boolean isCameraMovedManually;
    @NonNull
    private final SingleLiveEvent<Void> moveMapCameraToLocationEvent = new SingleLiveEvent<>();
    private boolean needToCenterMapCameraOnLocation;
    @NonNull
    private final SingleLiveEvent<CameraPosition> cameraPositionSingEvent = new SingleLiveEvent<>();
    private boolean updateFabStyleOnly;

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
            mapCombine(isLocationPermissionGranted, locationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), isGpsEnabledLiveData.getValue(), cameraPositionSingEvent.getValue())
        );
        mapViewState.addSource(locationLiveData, location ->
           mapCombine(isLocationPermissionGrantedLiveData.getValue(), location, nearbyRestaurantsLiveData.getValue(), isGpsEnabledLiveData.getValue(), cameraPositionSingEvent.getValue())
        );
        mapViewState.addSource(nearbyRestaurantsLiveData, nearbyRestaurants ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), locationLiveData.getValue(), nearbyRestaurants, isGpsEnabledLiveData.getValue(), cameraPositionSingEvent.getValue())
        );
        mapViewState.addSource(isGpsEnabledLiveData, isGpsEnabled ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), locationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), isGpsEnabled, cameraPositionSingEvent.getValue())
        );
        mapViewState.addSource(moveMapCameraToLocationEvent, moveMapCameraToLocation ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), locationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), isGpsEnabledLiveData.getValue(), cameraPositionSingEvent.getValue())
        );
        mapViewState.addSource(cameraPositionSingEvent, cameraPosition ->
            mapCombine(isLocationPermissionGrantedLiveData.getValue(), locationLiveData.getValue(), nearbyRestaurantsLiveData.getValue(), isGpsEnabledLiveData.getValue(), cameraPosition)
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

        // Setup markers to add
        final List<MarkerViewState> markers;
        if (needToCenterMapCameraOnLocation) {
            isCameraMovedManually = false; // Reset flag because camera needs to be moved programmatically
            needToCenterMapCameraOnLocation = false;

            if (!isGpsCurrentlyEnabled) {
                requestGpsUseCase.request();
                return;
            }
            markers = null; // No markers to add when the map camera is moved to the current location
        } else if (updateFabStyleOnly) {
            markers = null;
        } else {
            markers = setupMarkers(nearbyRestaurants);
        }

        // Setup map coordinates (weather or not the map camera should be moved)
        final Double latitude, longitude;
        final Float zoom;
        if (!isCameraMovedManually && !updateFabStyleOnly && isGpsCurrentlyEnabled && location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            zoom = Math.max(STREET_ZOOM_LEVEL, currentZoom);
        } else {
            latitude = null;
            longitude = null;
            zoom = null;
        }

        if (updateFabStyleOnly) {
            updateFabStyleOnly = false; // Reset flag
        }

        // Setup FAB style
        final FabStyle fabStyle = setupFabStyle(isGpsCurrentlyEnabled, location, cameraPosition);

        // Set view state
        System.out.println("before setViewState: " + latitude + ", " + longitude + ", " + zoom);
        mapViewState.setValue(new MapViewState(
            isLocationGranted && isGpsCurrentlyEnabled,
            isLocationGranted,
            fabStyle.getFabDrawable(),
            fabStyle.getFabColor(),
            markers,
            latitude,
            longitude,
            zoom
        ));
    }

    private FabStyle setupFabStyle(boolean isGpsEnabled,
                                   @Nullable Location currentLocation,
                                   @Nullable CameraPosition cameraPosition
    ) {
        final int fabDrawable;
        final int fabColor;

        if (!isGpsEnabled) {
            fabDrawable = R.drawable.ic_gps_off;
            fabColor = android.R.color.holo_red_dark;
        } else {
            fabDrawable = R.drawable.ic_gps_on;
            fabColor = isMapCenteredOnCurrentLocation(cameraPosition, currentLocation) ?
                R.color.blue_google :
                R.color.black;
        }

        return new FabStyle(fabDrawable, fabColor);
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

    public void setCameraMovedManually(int reason) {
        isCameraMovedManually = reason != GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION;
    }

    public void onCameraStopped(@NonNull CameraPosition newCameraPosition) {
        updateFabStyleOnly = true;
        cameraPositionSingEvent.setValue(newCameraPosition); // This event is a source that triggers the MapViewState's update
    }

    public void onLocationButtonClicked() {
        needToCenterMapCameraOnLocation = true;
        moveMapCameraToLocationEvent.call(); // This event is a source that triggers the MapViewState's update
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private boolean isMapCenteredOnCurrentLocation(@Nullable CameraPosition cameraPosition, @Nullable Location currentLocation) {
        if (cameraPosition == null || currentLocation == null) {
            return false;
        }

        // Better use BigDecimal to compare coordinates rather than double values
        final BigDecimal locationLat = toBigDecimal(currentLocation.getLatitude());
        final BigDecimal locationLng = toBigDecimal(currentLocation.getLongitude());
        final BigDecimal mapCameraLat = toBigDecimal(cameraPosition.target.latitude);
        final BigDecimal mapCameraLng = toBigDecimal(cameraPosition.target.longitude);

        return locationLat.compareTo(mapCameraLat) == 0 && locationLng.compareTo(mapCameraLng) == 0;
    }

    private BigDecimal toBigDecimal(double coordinate) {
        return BigDecimal.valueOf(coordinate).setScale(4, RoundingMode.HALF_UP);
    }
}
