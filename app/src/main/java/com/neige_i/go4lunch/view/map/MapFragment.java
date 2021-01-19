package com.neige_i.go4lunch.view.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.neige_i.go4lunch.R;

import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get notified when the map is ready to be used
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        // Get fused location to retrieve current user position (latitude and longitude)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        Places.initialize(requireContext(), "AIzaSyDdc24VwRd5iGQjd46ygpOIcVGhiDnD4gs");
        placesClient = Places.createClient(requireContext());

        requireView().findViewById(R.id.location_btn).setOnClickListener(v -> centerCameraToCurrentLocation());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Request location permission if not granted yet
        if (isLocationPermissionGranted())
            enableLocation();
    }

    private boolean isLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    private void enableLocation() {
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @SuppressLint("MissingPermission")
    private void centerCameraToCurrentLocation() {
        if (isLocationPermissionGranted()) {
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

                    final List<Place.Field> placeFields = Arrays.asList(Place.Field.TYPES);//Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    placesClient.findCurrentPlace(FindCurrentPlaceRequest.newInstance(placeFields))
                        .addOnSuccessListener(requireActivity(), findCurrentPlaceResponse -> {
                            Log.d("Neige", "place count: " +
                                findCurrentPlaceResponse.getPlaceLikelihoods().size());
                    });
                }
            });
        }
    }
}
