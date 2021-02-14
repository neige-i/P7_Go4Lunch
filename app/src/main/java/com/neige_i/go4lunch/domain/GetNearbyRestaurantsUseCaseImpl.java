package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.domain.model.MapModel;

public class GetNearbyRestaurantsUseCaseImpl implements GetNearbyRestaurantsUseCase {

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final NearbyRepository nearbyRepository;

    public GetNearbyRestaurantsUseCaseImpl(@NonNull LocationRepository locationRepository, @NonNull NearbyRepository nearbyRepository) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = nearbyRepository;
    }

    @NonNull
    @Override
    public LiveData<MapModel> getNearby() {
        return Transformations.switchMap(locationRepository.getLocationPermission(), isPermissionGranted -> {
            final MutableLiveData<MapModel> mapModel = new MutableLiveData<>();

            if (!isPermissionGranted) {
                mapModel.setValue(new MapModel(isPermissionGranted, null, null));
                return mapModel;
            } else {
                return Transformations.switchMap(locationRepository.getCurrentLocation(), location -> {
                    if (location == null) {
                        mapModel.setValue(new MapModel(isPermissionGranted, location, null));
                        return mapModel;
                    } else {
                        return Transformations.map(nearbyRepository.getNearbyResponse(location), nearbyResponse -> new MapModel(
                            isPermissionGranted,
                            location,
                            nearbyResponse
                        ));
                    }
                });
            }
        });
    }
}
