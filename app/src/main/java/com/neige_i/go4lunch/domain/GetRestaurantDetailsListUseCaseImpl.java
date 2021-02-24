package com.neige_i.go4lunch.domain;

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
import com.neige_i.go4lunch.domain.model.ListModel;

import java.util.HashMap;
import java.util.Map;

public class GetRestaurantDetailsListUseCaseImpl implements GetRestaurantDetailsListUseCase {

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final NearbyRepository nearbyRepository;
    @NonNull
    private final DetailsRepository detailsRepository;

    private final MediatorLiveData<ListModel> listModelMediatorLiveData = new MediatorLiveData<>();

    private final MediatorLiveData<Map<String, DetailsResponse>> placeIdDetailsResponseMapMediatorLiveData = new MediatorLiveData<>();

    private final List<String> currentPla

    public GetRestaurantDetailsListUseCaseImpl(@NonNull LocationRepository locationRepository,
                                               @NonNull NearbyRepository nearbyRepository,
                                               @NonNull DetailsRepository detailsRepository
    ) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = nearbyRepository;
        this.detailsRepository = detailsRepository;

        placeIdDetailsResponseMapMediatorLiveData.setValue(new HashMap<>());

        // 1. FETCH the current location
        LiveData<NearbyResponse> nearbyResponseLiveData = Transformations.switchMap(
            locationRepository.getCurrentLocation(),
            currentLocation -> nearbyRepository.getNearbyResponse(currentLocation)
        );

        listModelMediatorLiveData.addSource(
            nearbyResponseLiveData,
            nearbyResponse -> combine(nearbyResponse, placeIdDetailsResponseMapMediatorLiveData.getValue())
        );
        listModelMediatorLiveData.addSource(
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

            for (int i = 0; i < nearbyResponse.getResults().size(); i++) {
                final NearbyResponse.Result result = nearbyResponse.getResults().get(i);

                if (placeIdDetailsResponseMap.get(result.getPlaceId()) == null) {
                    // 3. FETCH the restaurant details with the place ID for each one of them
//                            final int finalI = i;
                    final LiveData<DetailsResponse> detailsResponseLiveData = detailsRepository.getDetailsResponse(result.getPlaceId());

                    placeIdDetailsResponseMapMediatorLiveData.addSource(detailsResponseLiveData, newDetailsResponse -> {

                        // 4. ADD the new details response to the list and UPDATE LiveData
                        Map<String, DetailsResponse> map = placeIdDetailsResponseMapMediatorLiveData.getValue();

                        assert map != null;

                        map.put(newDetailsResponse.getResult().getPlaceId(), newDetailsResponse);

                        placeIdDetailsResponseMapMediatorLiveData.setValue(map);
                    });
                }
            }
        }

        listModelMediatorLiveData.setValue(...);
    }

    @NonNull
    @Override
    public LiveData<ListModel> getDetailsList() {
        return listModelMediatorLiveData;
    }
}
