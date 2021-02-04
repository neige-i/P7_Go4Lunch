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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.OnDetailQueriedCallback;
import com.neige_i.go4lunch.view.ViewModelFactory;

public class MapFragment extends Fragment {

    private GoogleMap googleMap;
    private OnDetailQueriedCallback onDetailQueriedCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDetailQueriedCallback = (OnDetailQueriedCallback) context;
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
            googleMap.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                return false;
            });
            googleMap.setOnInfoWindowClickListener(marker -> onDetailQueriedCallback.onDetailQueried((String) marker.getTag()));
            viewModel.onMapAvailable();
        });

        // Config FAB
        final FloatingActionButton fab = requireView().findViewById(R.id.location_btn);
        fab.setOnClickListener(v -> viewModel.onCurrentLocationQueried());

        // Update my-location layer
        viewModel.isLocationLayerEnabled().observe(requireActivity(), isEnabled -> {
            googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Disabled, replaced by FAB
            googleMap.getUiSettings().setMapToolbarEnabled(false); // Disable navigation options
            googleMap.setMyLocationEnabled(isEnabled);
            fab.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        });

        viewModel.getViewState().observe(requireActivity(), mapViewStates -> {
            // Change the size of the marker
            final Bitmap smallMarker = Bitmap.createScaledBitmap(
                ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_orange, null)).getBitmap(),
                100,
                100,
                false
            );

            // Add markers for all nearby restaurants
            for (MapViewState mapViewState : mapViewStates) {
                googleMap.addMarker(
                    new MarkerOptions()
                        .position(new LatLng(mapViewState.getLatitude(), mapViewState.getLongitude()))
                        .title(mapViewState.getName())
                        .snippet(mapViewState.getVicinity())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                ).setTag(mapViewState.getPlaceId());
            }
        });

        // Move camera to current user location
        viewModel.getZoomMapToCurrentLocationEvent().observe(requireActivity(), location ->
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
                ),
                15 // TODO: original behaviour zooms in only if current zoom level is lower than 15
            )));
    }
}
