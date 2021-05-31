package com.neige_i.go4lunch.view.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.neige_i.go4lunch.view.ViewModelFactory;

public class MapFragment extends Fragment {

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    private OnDetailsQueriedCallback onDetailsQueriedCallback;
//    /**
//     * Whenever a view state is available from the ViewModel, a list of the markers to display is provided.
//     * This variable prevents adding an already displayed marker when the view state is updated.
//     */
//    @NonNull
//    private final List<String> displayedMarkerIds = new ArrayList<>();

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
        final MapViewModel viewModel =
            new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewModel.class);

        // Init view binding
        final FragmentMapBinding binding = FragmentMapBinding.bind(view);

        // Setup UI
        final SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId()));
        mapFragment.getMapAsync(googleMap -> {
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
        Log.d("Neige", "MapFragment::setupMap: map is now ready");
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Replaced by custom FAB
        googleMap.getUiSettings().setMapToolbarEnabled(false); // Disable navigation options

//        viewModel.clearCurrentMarkers();

        // Setup marker click events
        googleMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return false; // Let the default behaviour occur
        });
        googleMap.setOnInfoWindowClickListener(marker -> {
            onDetailsQueriedCallback.onDetailsQueried((String) marker.getTag());
        });

        // Setup camera move events
        googleMap.setOnCameraMoveStartedListener(reason -> viewModel.setCameraMovedManually(reason));
        googleMap.setOnCameraIdleListener(() -> viewModel.onCameraStopped(googleMap.getCameraPosition()));
    }

    @SuppressLint("MissingPermission")
    private void updateMap(@NonNull MapViewState mapViewState, @NonNull GoogleMap googleMap, @NonNull FloatingActionButton locationBtn) {
        Log.d("Neige", "updateMap() called with: mapViewState = [" + mapViewState + "]");
        // Update location layer
        googleMap.setMyLocationEnabled(mapViewState.isLocationLayerEnabled());

        // Update FAB
        locationBtn.setVisibility(mapViewState.isFabVisible() ? View.VISIBLE : View.GONE);
        locationBtn.setImageResource(mapViewState.getFabDrawable());
        locationBtn.setColorFilter(getResources().getColor(mapViewState.getFabColor()));

        // Add markers for all nearby restaurants
        if (mapViewState.getMarkersToAdd() != null) {
            final Bitmap smallMarker = Bitmap.createScaledBitmap(
                ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_orange, null)).getBitmap(),
                100,
                100,
                false
            );

            googleMap.clear();
            for (MarkerViewState markerViewState : mapViewState.getMarkersToAdd()) {
                googleMap.addMarker(
                    new MarkerOptions()
                        .position(new LatLng(markerViewState.getLatitude(), markerViewState.getLongitude()))
                        .title(markerViewState.getName())
                        .snippet(markerViewState.getAddress())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                ).setTag(markerViewState.getPlaceId());
            }
        }

        // Move camera only if coordinates are not null
        if (mapViewState.getMapLatitude() != null && mapViewState.getMapLongitude() != null && mapViewState.getMapZoom() != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mapViewState.getMapLatitude(), mapViewState.getMapLongitude()),
                mapViewState.getMapZoom()
            ));
        }
    }
}
