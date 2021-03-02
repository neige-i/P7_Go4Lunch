package com.neige_i.go4lunch.domain;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.data.location.LocationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRestaurantDetailsListUseCaseImpl implements GetRestaurantDetailsListUseCase {

    @NonNull
    private final DetailsRepository detailsRepository;

    @NonNull
    private final MediatorLiveData<ListWrapper> listMediatorLiveData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<Map<String, DetailsResponse>> placeIdDetailsResponseMapMediatorLiveData = new MediatorLiveData<>();
    @NonNull
    private final List<String> queriedPlaceIds = new ArrayList<>();
    @Nullable
    private Location deviceLocation;

    public GetRestaurantDetailsListUseCaseImpl(@NonNull LocationRepository locationRepository,
                                               @NonNull NearbyRepository nearbyRepository,
                                               @NonNull DetailsRepository detailsRepository
    ) {
        this.detailsRepository = detailsRepository;

        placeIdDetailsResponseMapMediatorLiveData.setValue(new HashMap<>());

        // 1. FETCH the current location
        final LiveData<NearbyResponse> nearbyResponseLiveData = Transformations.switchMap(
            locationRepository.getCurrentLocation(),
            currentLocation -> {
                deviceLocation = currentLocation;
                return nearbyRepository.getNearbyResponse(currentLocation);
            }
        );

        listMediatorLiveData.addSource(
            nearbyResponseLiveData,
            nearbyResponse -> combine(nearbyResponse, placeIdDetailsResponseMapMediatorLiveData.getValue())
        );
        listMediatorLiveData.addSource(
            placeIdDetailsResponseMapMediatorLiveData,
            placeIdDetailsResponseMap -> combine(nearbyResponseLiveData.getValue(), placeIdDetailsResponseMap)
        );
    }

    private void combine(@Nullable NearbyResponse nearbyResponse, @Nullable Map<String, DetailsResponse> placeIdDetailsResponseMap) {
        if (nearbyResponse == null) {
            return;
        }

        if (placeIdDetailsResponseMap == null) {
            throw new IllegalStateException("Impossible state : map is always initialized !");
        }

        if (nearbyResponse.getResults() != null) {

            for (NearbyResponse.Result result : nearbyResponse.getResults()) {
                final String placeId = result.getPlaceId();

                if (!queriedPlaceIds.contains(placeId)) { // ASKME: check if map contains key

                    queriedPlaceIds.add(placeId);

                    // 3. FETCH the restaurant details with the place ID for each one of them
                    placeIdDetailsResponseMapMediatorLiveData.addSource(
                        detailsRepository.getDetailsResponse(placeId),
                        newDetailsResponse -> {

                            // 4. ADD the new details response to the list and UPDATE LiveData

                            // ASKME: put result's placeId instead of query placeId
                            placeIdDetailsResponseMap.put(placeId, newDetailsResponse);

                            placeIdDetailsResponseMapMediatorLiveData.setValue(placeIdDetailsResponseMap);
                        }
                    );
                }
            }
        }

        listMediatorLiveData.setValue(new ListWrapper(
            nearbyResponse,
            new ArrayList<>(placeIdDetailsResponseMap.values()),
            deviceLocation
        ));
    }

    @NonNull
    @Override
    public LiveData<ListWrapper> getDetailsList() {
        return listMediatorLiveData;
    }
}
