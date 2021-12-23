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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
class MapViewModel extends ViewModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    static final float DEFAULT_ZOOM_LEVEL = 15; // 2=world, 5=continent, 10=city, 15=streets, 20=buildings

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

    @Nullable
    private Location lastKnownCenteredLocation;
    private boolean searchedRestaurantPing;

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
            searchedRestaurantPing = true;
            System.out.println("addSource");
            combine(mapData, currentPositionMutableLiveData.getValue(), onLocationButtonClickedPing.getValue());
        });
        mapViewState.addSource(currentPositionMutableLiveData, currentPosition -> combine(mapDataLiveData.getValue(), currentPosition, onLocationButtonClickedPing.getValue()));
        mapViewState.addSource(onLocationButtonClickedPing, locationButtonPing -> combine(mapDataLiveData.getValue(), currentPositionMutableLiveData.getValue(), locationButtonPing));
    }

    @NonNull
    public LiveData<MapViewState> getMapViewState() {
        return mapViewState;
    }

    @NonNull
    public LiveData<String> getShowDetailsEvent() {
        return showDetailsEvent;
    }

    // ------------------------------------ VIEW STATE METHODS -------------------------------------

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
        final boolean isLocationAvailable = isGpsEnabled && currentLocation != null;
        final boolean isMapCurrentlyCenteredOnLocation = isSameLatLng(currentPosition, currentLocation);

        // Respond to location button click
        if (Objects.equals(locationButtonPing, true)) {
            onLocationButtonClickedPing.setValue(null); // Reset ping

            lastKnownCenteredLocation = null; // Reset flag

            if (!isGpsEnabled) {
                requestGpsUseCase.request();
                return;
            }
        }

        setupMarkers(mapData.getMapRestaurants());

        System.out.println("in comine");
        // Setup CameraPosition
        final double cameraLat;
        final double cameraLng;
        final float cameraZoom;
        final float maxZoom = Math.max(DEFAULT_ZOOM_LEVEL, currentPosition.zoom);
        if (mapData.getSearchedRestaurantLocation() != null && searchedRestaurantPing) {
            searchedRestaurantPing = false; // Reset flag

            cameraLat = mapData.getSearchedRestaurantLocation().getLatitude();
            cameraLng = mapData.getSearchedRestaurantLocation().getLongitude();
            cameraZoom = maxZoom;
        } else if (needToMoveMapToLocation(currentPosition, currentLocation) && isLocationAvailable) {
            cameraLat = currentLocation.getLatitude();
            cameraLng = currentLocation.getLongitude();
            cameraZoom = maxZoom;
        } else {
            cameraLat = currentPosition.target.latitude;
            cameraLng = currentPosition.target.longitude;
            cameraZoom = currentPosition.zoom;
        }

        updateLastKnownCenteredLocation(currentPosition, currentLocation, isMapCurrentlyCenteredOnLocation);

        // Set view state
        mapViewState.setValue(new MapViewState(
            isLocationPermissionGranted && isGpsEnabled,
            isLocationPermissionGranted,
            isGpsEnabled ? R.drawable.ic_gps_on : R.drawable.ic_gps_off,
            getFabColor(isLocationAvailable, isMapCurrentlyCenteredOnLocation),
            new ArrayList<>(displayedMarkers.values()),
            cameraLat,
            cameraLng,
            cameraZoom
        ));
    }

    private void setupMarkers(@NonNull List<MapRestaurant> mapRestaurants) {
        for (MapRestaurant mapRestaurant : mapRestaurants) {
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
    }

    private boolean needToMoveMapToLocation(
        @NonNull CameraPosition currentPosition,
        @Nullable Location currentLocation
    ) {
        final boolean hasLocationChanged = currentLocation != lastKnownCenteredLocation;
        final boolean isFirstMapCentering = lastKnownCenteredLocation == null;
        final boolean wasMapCenteredOnLocation = isSameLatLng(currentPosition, lastKnownCenteredLocation);

        return hasLocationChanged && (isFirstMapCentering || wasMapCenteredOnLocation);
    }

    private void updateLastKnownCenteredLocation(
        @NonNull CameraPosition currentPosition,
        @Nullable Location currentLocation,
        boolean isMapCurrentlyCenteredOnLocation
    ) {
        final boolean isMapZoomedEnough = currentPosition.zoom >= DEFAULT_ZOOM_LEVEL;

        if (isMapCurrentlyCenteredOnLocation && isMapZoomedEnough) {
            lastKnownCenteredLocation = currentLocation;
        }
    }

    @ColorRes
    private int getFabColor(boolean isLocationAvailable, boolean isMapCurrentlyCenteredOnLocation) {
        if (!isLocationAvailable) {
            return android.R.color.holo_red_dark;
        } else if (isMapCurrentlyCenteredOnLocation) {
            return R.color.blue_google;
        } else {
            return R.color.black;
        }
    }

    // ---------------------------------------- MAP METHODS ----------------------------------------

    public void onCameraStopped(@NonNull CameraPosition newCameraPosition) {
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
        final CameraPosition currentCameraPosition = currentPositionMutableLiveData.getValue();

        if (!isSameLatLng(newCameraPosition, currentCameraPosition) ||
            newCameraPosition.zoom != currentCameraPosition.zoom
        ) {
            currentPositionMutableLiveData.setValue(newCameraPosition);
        }
    }

    public void onLocationButtonClicked() {
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

    private <T> boolean isSameLatLng(
        @NonNull CameraPosition nonNullCameraPosition,
        @Nullable T nullablePositionObject
    ) {
        if (nullablePositionObject == null) {
            return false;
        }

        final int scale = getScale(nonNullCameraPosition.zoom);

        final BigDecimal lat1 = toBigDecimal(nonNullCameraPosition.target.latitude, scale);
        final BigDecimal lng1 = toBigDecimal(nonNullCameraPosition.target.longitude, scale);

        final double[] latLng = getLatLng(nullablePositionObject);
        final BigDecimal lat2 = toBigDecimal(latLng[0], scale);
        final BigDecimal lng2 = toBigDecimal(latLng[1], scale);

        return lat1.compareTo(lat2) == 0 && lng1.compareTo(lng2) == 0;
    }

    @NonNull
    private <T> double[] getLatLng(@NonNull T positionObject) {
        if (positionObject instanceof CameraPosition) {
            return new double[]{
                ((CameraPosition) positionObject).target.latitude,
                ((CameraPosition) positionObject).target.longitude,
            };
        } else if (positionObject instanceof Location) {
            return new double[]{
                ((Location) positionObject).getLatitude(),
                ((Location) positionObject).getLongitude(),
            };
        } else {
            throw new IllegalArgumentException("Unknown position object: " + positionObject.getClass().getSimpleName());
        }
    }

    private int getScale(float currentZoom) {
        return (int) Math.round(currentZoom * .3 - 2);
    }

    @NonNull
    private BigDecimal toBigDecimal(double mapCoordinate, int scale) {
        return BigDecimal.valueOf(mapCoordinate).setScale(scale, RoundingMode.HALF_UP);
    }
}
