package com.neige_i.go4lunch.view.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.FragmentMapBinding;
import com.neige_i.go4lunch.view.OnDetailsQueriedCallback;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    private MapViewModel viewModel;
    private OnDetailsQueriedCallback onDetailsQueriedCallback;
    @NonNull
    private final List<String> displayedMarkerIds = new ArrayList<>();

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDetailsQueriedCallback = (OnDetailsQueriedCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId()))
            .getMapAsync(googleMap -> {
                initMap(googleMap, viewModel);

                // Update UI when state is changed
                // Start observing the view state INSIDE this lambda to make sure the map is ready
                viewModel.getMapViewState().observe(getViewLifecycleOwner(), mapViewState ->
                    updateMap(mapViewState, googleMap, binding.locationBtn)
                );
            });

        binding.locationBtn.setOnClickListener(v -> viewModel.onLocationButtonClicked());
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
            onDetailsQueriedCallback.onDetailsQueried((String) marker.getTag());
        });

        // Setup camera move events
        googleMap.setOnCameraMoveStartedListener(reason -> viewModel.onCameraMoved(reason));
        googleMap.setOnCameraIdleListener(() -> {
            viewModel.onCameraStopped(googleMap.getCameraPosition());
        });
    }

    @SuppressLint("MissingPermission")
    private void updateMap(@NonNull MapViewState mapViewState,
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
        for (MarkerViewState markerViewState : mapViewState.getMarkers()) {
            final String placeId = markerViewState.getPlaceId();

            if (!displayedMarkerIds.contains(placeId)) {
                googleMap.addMarker(
                    new MarkerOptions()
                        .position(new LatLng(markerViewState.getLatitude(), markerViewState.getLongitude()))
                        .title(markerViewState.getName())
                        .snippet(markerViewState.getAddress())
                        .icon(BitmapDescriptorFactory.fromBitmap(getSmallMarker(R.drawable.ic_marker_orange)))
                ).setTag(placeId);

                displayedMarkerIds.add(placeId);
            }
        }

        // Move map camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
            new LatLng(mapViewState.getMapLatitude(), mapViewState.getMapLongitude()),
            mapViewState.getMapZoom()
        ));
    }

    private Bitmap getSmallMarker(@DrawableRes int drawableId) {
        final Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableId, null);
        return Bitmap.createScaledBitmap(
            ((BitmapDrawable) drawable).getBitmap(),
            100,
            100,
            false
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.onFragmentResumed();
    }
}
