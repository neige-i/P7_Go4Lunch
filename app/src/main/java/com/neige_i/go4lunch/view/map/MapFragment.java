package com.neige_i.go4lunch.view.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.FragmentMapBinding;
import com.neige_i.go4lunch.view.StartDetailActivityCallback;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    private MapViewModel viewModel;
    private StartDetailActivityCallback startDetailActivityCallback;
    @NonNull
    private final Map<String, Marker> displayedMarkers = new HashMap<>();

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        startDetailActivityCallback = (StartDetailActivityCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        // Init view binding
        final FragmentMapBinding binding = FragmentMapBinding.bind(view);

        // Setup UI
        final SupportMapFragment supportMapFragment =
            (SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId());
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(googleMap -> {
                initMap(googleMap, viewModel);

                // Update UI when state is changed
                // Start observing the view state INSIDE this lambda to make sure the map is ready
                viewModel.getMapViewState().observe(getViewLifecycleOwner(), mapViewState ->
                    updateMap(mapViewState, googleMap, binding.locationBtn)
                );
            });
        }

        binding.locationBtn.setOnClickListener(v -> viewModel.onLocationButtonClicked());

        // Update UI when events are triggered
        viewModel.getShowDetailsEvent().observe(getViewLifecycleOwner(), placeId -> {
            startDetailActivityCallback.showDetailedInfo(placeId);
        });
    }

    private void initMap(@NonNull GoogleMap googleMap, @NonNull MapViewModel viewModel) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Replaced by custom FAB
        googleMap.getUiSettings().setMapToolbarEnabled(false); // Disable navigation options

        // Setup marker click events
        googleMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return false; // Let the default behaviour occur
        });
        googleMap.setOnInfoWindowClickListener(marker -> {
            viewModel.onInfoWindowClick((String) marker.getTag());
        });

        // Setup camera move events
        googleMap.setOnCameraMoveStartedListener(reason -> viewModel.onCameraMoved(reason));
        googleMap.setOnCameraIdleListener(() -> {
            viewModel.onCameraStopped(googleMap.getCameraPosition());
        });
    }

    @SuppressLint("MissingPermission")
    private void updateMap(
        @NonNull MapViewState mapViewState,
        @NonNull GoogleMap googleMap,
        @NonNull FloatingActionButton locationBtn
    ) {
        // Update location layer
        googleMap.setMyLocationEnabled(mapViewState.isLocationLayerEnabled());

        // Update FAB
        locationBtn.setVisibility(mapViewState.isFabVisible() ? View.VISIBLE : View.GONE);
        locationBtn.setImageResource(mapViewState.getFabDrawable());
        locationBtn.setColorFilter(getResources().getColor(mapViewState.getFabColor()));

        // Add markers for all nearby restaurants
        if (mapViewState.isClearMarkers()) {
            displayedMarkers.clear();
            googleMap.clear();
        }
        for (MarkerViewState markerViewState : mapViewState.getMarkers()) {
            final String placeId = markerViewState.getPlaceId();

            final Marker currentMarker = displayedMarkers.get(placeId);
            if (currentMarker == null) {
                final Marker marker = googleMap.addMarker(
                    new MarkerOptions()
                        .position(new LatLng(markerViewState.getLatitude(), markerViewState.getLongitude()))
                        .title(markerViewState.getName())
                        .snippet(markerViewState.getAddress())
                        .icon(getSmallerIcon(markerViewState.getMarkerDrawable()))
                );

                if (marker != null) {
                    marker.setTag(placeId);
                    displayedMarkers.put(placeId, marker);
                }
            } else {
                currentMarker.setIcon(getSmallerIcon(markerViewState.getMarkerDrawable()));
            }
        }

        // Move map camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
            new LatLng(mapViewState.getMapLatitude(), mapViewState.getMapLongitude()),
            mapViewState.getMapZoom()
        ));
    }

    private BitmapDescriptor getSmallerIcon(@DrawableRes int drawableId) {
        return BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(getResources(), drawableId),
            100,
            100,
            false
        ));
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.onFragmentResumed();
    }
}
