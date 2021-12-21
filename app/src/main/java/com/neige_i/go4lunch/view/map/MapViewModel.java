package com.neige_i.go4lunch.view.map;

import android.location.Location;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.map.GetMapDataUseCase;
import com.neige_i.go4lunch.domain.map.MapData;
import com.neige_i.go4lunch.domain.map.MapRestaurant;
import com.neige_i.go4lunch.domain.map.RequestGpsUseCase;
import com.neige_i.go4lunch.view.SingleLiveEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class MapViewModel extends ViewModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    static final float DEFAULT_ZOOM_LEVEL = 18; // 2=world, 5=continent, 10=city, 15=streets, 20=buildings

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final GetMapDataUseCase getMapDataUseCase;
    @NonNull
    private final RequestGpsUseCase requestGpsUseCase;

    // ----------------------------------- LIVE DATA TO OBSERVE ------------------------------------

    @NonNull
    private final MediatorLiveData<MapViewState> mapViewState = new MediatorLiveData<>();
    @NonNull
    private final SingleLiveEvent<String> showDetailsEvent = new SingleLiveEvent<>();

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Source {@code LiveData} to update {@link #mapViewState}.
     */
    @NonNull
    private final MutableLiveData<CameraPosition> currentPositionMutableLiveData = new MutableLiveData<>();
    /**
     * Source {@code LiveData} to update {@link #mapViewState}.
     */
    @NonNull
    private final MutableLiveData<Boolean> onLocationButtonClickedPing = new MutableLiveData<>();

    /**
     * Set of all the markers to display on the map. When the current location changes, the nearby
     * markers are added to current ones instead of replacing them. Using a {@code Set} instead
     * of a {@code List} prevents adding duplicate {@link MarkerViewState}.
     */
    @NonNull
    private final Map<String, MarkerViewState> displayedMarkers = new HashMap<>();

    /**
     * Flag to make the map camera follows the current location only if the map has not been manually
     * moved by the user. Initially, the map camera was automatically moved whenever a new location
     * became available. But if the user manually scrolls the map, it should stay still even if the
     * location is updated.
     */
    private boolean keepMapCenteredOnLocation = true;
    /**
     * Current zoom level. Used to compute the scale when comparing latitudes and longitudes.
     */
    private float currentZoom;
    private boolean mapDataPing;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    @Inject
    MapViewModel(
        @NonNull GetMapDataUseCase getMapDataUseCase,
        @NonNull RequestGpsUseCase requestGpsUseCase
    ) {
        this.getMapDataUseCase = getMapDataUseCase;
        this.requestGpsUseCase = requestGpsUseCase;

        final LiveData<MapData> mapDataLiveData = getMapDataUseCase.get();

        mapViewState.addSource(mapDataLiveData, mapData -> {
            mapDataPing = true;
            combine(mapData, currentPositionMutableLiveData.getValue(), onLocationButtonClickedPing.getValue());
        });
        mapViewState.addSource(currentPositionMutableLiveData, currentPosition -> combine(mapDataLiveData.getValue(), currentPosition, onLocationButtonClickedPing.getValue()));
        mapViewState.addSource(onLocationButtonClickedPing, locationButtonPing -> combine(mapDataLiveData.getValue(), currentPositionMutableLiveData.getValue(), locationButtonPing));
    }

    private void combine(
        @Nullable MapData mapData,
        @Nullable CameraPosition currentPosition,
        @Nullable Boolean locationButtonPing
    ) {
        if (mapData == null || currentPosition == null) {
            return;
        }

        final boolean isLocationPermissionGranted = mapData.isLocationPermissionGranted();
        final boolean isGpsEnabled = mapData.isGpsEnabled();
        final Location currentLocation = mapData.getCurrentLocation();

        // Setup whether or not the map camera should be moved
        final boolean moveMapToLocation;
        if (Objects.equals(locationButtonPing, true)) {
            onLocationButtonClickedPing.setValue(null); // Reset ping

            if (!isGpsEnabled) {
                requestGpsUseCase.request();
                return;
            } else {
                moveMapToLocation = currentLocation != null;
            }
        } else {
            moveMapToLocation = keepMapCenteredOnLocation && isGpsEnabled && currentLocation != null;
        }

        // Setup markers
        for (MapRestaurant mapRestaurant : mapData.getMapRestaurants()) {
            final String restaurantId = mapRestaurant.getPlaceId();

            final boolean isSearched = mapRestaurant.isSearched();

            final int markerDrawable;
            if (mapRestaurant.getInterestedWorkmateCount() > 0) {
                markerDrawable = isSearched ? R.drawable.ic_marker_green_search : R.drawable.ic_marker_green;
            } else {
                markerDrawable = isSearched ? R.drawable.ic_marker_orange_search : R.drawable.ic_marker_orange;
            }

            final MarkerViewState markerViewState = new MarkerViewState(
                restaurantId,
                mapRestaurant.getName(),
                mapRestaurant.getLatitude(),
                mapRestaurant.getLongitude(),
                mapRestaurant.getAddress(),
                markerDrawable,
                isSearched ? 300 : 100
            );
            displayedMarkers.put(restaurantId, markerViewState);
        }

        // Setup FAB color
        @ColorRes final int fabColor;
        if (currentLocation == null || !isGpsEnabled) {
            fabColor = android.R.color.holo_red_dark;
        } else if (equalsCurrentPosition(currentLocation.getLatitude(), currentLocation.getLongitude())) {
            fabColor = R.color.blue_google;
        } else {
            fabColor = R.color.black;
        }

        // Setup CameraPosition
        final double cameraLat;
        final double cameraLng;
        final float cameraZoom;
        if (mapData.getSearchedRestaurantLocation() != null && mapDataPing) {
            mapDataPing = false; // Reset flag

            keepMapCenteredOnLocation = false;

            cameraLat = mapData.getSearchedRestaurantLocation().getLatitude();
            cameraLng = mapData.getSearchedRestaurantLocation().getLongitude();
            cameraZoom = Math.max(DEFAULT_ZOOM_LEVEL, currentPosition.zoom);
        } else if (moveMapToLocation) {
            cameraLat = currentLocation.getLatitude();
            cameraLng = currentLocation.getLongitude();
            cameraZoom = Math.max(DEFAULT_ZOOM_LEVEL, currentPosition.zoom);
        } else {
            cameraLat = currentPosition.target.latitude;
            cameraLng = currentPosition.target.longitude;
            cameraZoom = currentPosition.zoom;
        }

        // Set view state
        mapViewState.setValue(new MapViewState(
            isLocationPermissionGranted && isGpsEnabled,
            isLocationPermissionGranted,
            isGpsEnabled ? R.drawable.ic_gps_on : R.drawable.ic_gps_off,
            fabColor,
            new ArrayList<>(displayedMarkers.values()),
            cameraLat,
            cameraLng,
            cameraZoom
        ));
    }

    @NonNull
    public LiveData<MapViewState> getMapViewState() {
        return mapViewState;
    }

    @NonNull
    public LiveData<String> getShowDetailsEvent() {
        return showDetailsEvent;
    }

    // ---------------------------------------- MAP METHODS ----------------------------------------

    public void onCameraMoved(int reason) {
        if (reason != GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            // Disable the "following" feature: the map stays still even if the location changes
            keepMapCenteredOnLocation = false;
        }
    }

    public void onCameraStopped(@NonNull CameraPosition newPosition) {
        // Retrieve current zoom
        currentZoom = newPosition.zoom;

        // Setting lastKnownCameraPositionMutableLiveData's value without any condition can lead to
        // an infinite loop because:
        // 1. This method updates the CameraPosition's LiveData value
        // 2. The combine() method is called and the MapViewState's LiveData value is set
        // 3. The MapFragment observes the MapViewState's LiveData and animates the Google map camera
        // 4. When the camera finishes moving, GoogleMap#setOnCameraIdleListener() is triggered
        // 5. The listener calls this method to update the camera position (loop -> back to step 1)
        // A solution would be to compare the current and new CameraPosition with equals() and update
        // the LiveData value only if the objects are not equal
        // BUT: the position retrieved from setOnCameraIdleListener() is slightly different
        // from the position previously given to move the camera
        // So both new and current CameraPosition's coordinates are compared using a custom method

        // currentPositionMutableLiveData.getValue() can't be null if equalsCurrentPosition() returns true
        //noinspection ConstantConditions
        if (!equalsCurrentPosition(newPosition.target.latitude, newPosition.target.longitude) ||
            currentPositionMutableLiveData.getValue().zoom != newPosition.zoom
        ) {
            currentPositionMutableLiveData.setValue(newPosition);
        }
    }

    public void onLocationButtonClicked() {
        keepMapCenteredOnLocation = true;
        onLocationButtonClickedPing.setValue(true);
    }

    public void onFragmentResumed() {
        getMapDataUseCase.refresh();
    }

    public void onInfoWindowClick(@Nullable String placeId) {
        if (placeId != null) {
            showDetailsEvent.setValue(placeId);
        }
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private boolean equalsCurrentPosition(double latitude, double longitude) {
        final CameraPosition currentPosition = currentPositionMutableLiveData.getValue();

        if (currentPosition == null) {
            return false;
        }

        return toBigDecimal(latitude).compareTo(toBigDecimal(currentPosition.target.latitude)) == 0 &&
            toBigDecimal(longitude).compareTo(toBigDecimal(currentPosition.target.longitude)) == 0;
    }

    private BigDecimal toBigDecimal(double mapCoordinate) {
        // Set scale according to current zoom (the formula was found experimentally)
        final int scale = (int) Math.round(currentZoom / 4.5 + 1);
        return BigDecimal.valueOf(mapCoordinate).setScale(scale, RoundingMode.HALF_UP);
    }
}
