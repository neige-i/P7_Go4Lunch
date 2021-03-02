package com.neige_i.go4lunch.domain;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.data.location.LocationRepository;

public class GetNearbyRestaurantsUseCaseImpl implements GetNearbyRestaurantsUseCase {

    @NonNull
    private final MediatorLiveData<MapWrapper> mapMediatorLiveData = new MediatorLiveData<>();

    @Nullable
    private Location deviceLocation;

    public GetNearbyRestaurantsUseCaseImpl(@NonNull LocationRepository locationRepository, @NonNull NearbyRepository nearbyRepository) {

        final LiveData<Boolean> locationPermissionLiveData = locationRepository.getLocationPermission();

        final LiveData<NearbyResponse> nearbyResponseLiveData = Transformations.switchMap(
            locationRepository.getCurrentLocation(), currentLocation -> {
                deviceLocation = currentLocation;
                return nearbyRepository.getNearbyResponse(currentLocation);
            }
        );

        mapMediatorLiveData.addSource(
            locationPermissionLiveData,
            isPermissionGranted -> combine(isPermissionGranted, nearbyResponseLiveData.getValue())
        );
        mapMediatorLiveData.addSource(
            nearbyResponseLiveData,
            nearbyResponse -> combine(locationPermissionLiveData.getValue(), nearbyResponse)
        );
    }

    private void combine(@Nullable Boolean isPermissionGranted, @Nullable NearbyResponse nearbyResponse) {
        if (isPermissionGranted == null)
            return;

        if (!isPermissionGranted)
            mapMediatorLiveData.setValue(new MapWrapper(false, null, null));
        else if (deviceLocation == null)
            mapMediatorLiveData.setValue(new MapWrapper(true, null, null));
        else
            mapMediatorLiveData.setValue(new MapWrapper(true, deviceLocation, nearbyResponse));
    }

    @NonNull
    @Override
    public LiveData<MapWrapper> getNearby() {
        return mapMediatorLiveData;
    }
}
