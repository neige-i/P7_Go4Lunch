package com.neige_i.go4lunch.view.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.ViewModelFactory;

public class MapFragment extends Fragment {

    private GoogleMap googleMap;

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
            viewModel.onMapAvailable();
        });

        // Config FAB
        final FloatingActionButton fab = requireView().findViewById(R.id.location_btn);
        fab.setOnClickListener(v -> viewModel.onCurrentLocationQueried());

        // Update my-location layer
        viewModel.isLocationLayerEnabled().observe(requireActivity(), isEnabled -> {
            googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Disabled, replaced by FAB
            googleMap.setMyLocationEnabled(isEnabled);
            fab.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
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
