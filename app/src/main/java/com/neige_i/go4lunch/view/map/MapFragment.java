package com.neige_i.go4lunch.view.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.ViewModelFactory;

public class MapFragment extends Fragment {

    private MapViewModel viewModel;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

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
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewModel.class);

        // Get notified when the map is ready to be used
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            viewModel.onMapAvailable();
        });

        // Get fused location to retrieve current user position (latitude and longitude)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

//        Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
//        placesClient = Places.createClient(requireContext());

        requireView().findViewById(R.id.location_btn).setOnClickListener(v -> centerCameraToCurrentLocation());
    }
        // Init UI component
        final FloatingActionButton fab = requireView().findViewById(R.id.location_btn);
        fab.setOnClickListener(v -> centerCameraToCurrentLocation());

        // Update my-location layer
        viewModel.isLocationLayerEnabled().observe(requireActivity(), isEnabled -> {
            googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Disabled, replaced by FAB
            googleMap.setMyLocationEnabled(isEnabled);
            fab.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        });
    }

    private boolean isLocationPermissionGranted() {
        return true;
    }

    @SuppressLint("MissingPermission")
    private void centerCameraToCurrentLocation() {
        if (isLocationPermissionGranted()) {
            // ASKME: is last known location really useful
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(
                            location.getLatitude(),
                            location.getLongitude()
                        ),
                        15 // TODO: original behaviour zooms in only if current zoom level is lower than 15
                    ));
                    Log.d("Neige", "last location: " + location.getLatitude() + " " + location.getLongitude());
                }
            });
        }
    }
}
