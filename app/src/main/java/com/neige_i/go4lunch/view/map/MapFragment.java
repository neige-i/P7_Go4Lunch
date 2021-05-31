package com.neige_i.go4lunch.view.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.util.OnDetailsQueriedCallback;
import com.neige_i.go4lunch.view.util.Util;
import com.neige_i.go4lunch.view.util.ViewModelFactory;

public class MapFragment extends Fragment {

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    private GoogleMap googleMap;
    private OnDetailsQueriedCallback onDetailsQueriedCallback;

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

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init ViewModel
        final MapViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewModel.class);

        // When the map is ready to be used, set the GoogleMap object and notify the ViewModel
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(googleMap -> {
            this.googleMap = googleMap;

            googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Replaced by custom FAB
            googleMap.getUiSettings().setMapToolbarEnabled(false); // Disable navigation options

            googleMap.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                return false;
            });
            googleMap.setOnInfoWindowClickListener(marker -> onDetailsQueriedCallback.onDetailsQueried((String) marker.getTag()));
            googleMap.setOnCameraIdleListener(() -> viewModel.onCameraIdled(googleMap.getCameraPosition().zoom));

            final CameraPosition cameraPosition = googleMap.getCameraPosition();
            viewModel.onMapAvailable(cameraPosition.target.latitude, cameraPosition.target.longitude, cameraPosition.zoom);
        });

        // Config FAB
        final FloatingActionButton fab = requireView().findViewById(R.id.location_btn);
        fab.setOnClickListener(v -> viewModel.onCameraCentered());

        // Update UI when state is changed
        viewModel.getViewState().observe(getViewLifecycleOwner(), mapViewState -> {
            googleMap.setMyLocationEnabled(mapViewState.isLocationLayerEnabled());
            fab.setVisibility(mapViewState.isLocationLayerEnabled() ? View.VISIBLE : View.GONE);

            // Change the size of the marker
            final Bitmap smallMarker = Bitmap.createScaledBitmap(
                ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_orange, null)).getBitmap(),
                100,
                100,
                false
            );

            // Add markers for all nearby restaurants
            for (MarkerViewState markerViewState : mapViewState.getMarkerViewStates()) {
                googleMap.addMarker(
                    new MarkerOptions()
                        .position(new LatLng(markerViewState.getLatitude(), markerViewState.getLongitude()))
                        .title(markerViewState.getName())
                        .snippet(Util.getShortAddress(markerViewState.getVicinity()))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                ).setTag(markerViewState.getPlaceId());
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mapViewState.getMapLatitude(), mapViewState.getMapLongitude()),
                mapViewState.getMapZoom()
            ));
        });
    }
}
