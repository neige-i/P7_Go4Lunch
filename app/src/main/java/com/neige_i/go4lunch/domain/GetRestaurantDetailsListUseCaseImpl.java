package com.neige_i.go4lunch.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.NearbyRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;
import com.neige_i.go4lunch.data.location.LocationRepository;
import com.neige_i.go4lunch.domain.model.ListModel;

import java.util.ArrayList;
import java.util.List;

public class GetRestaurantDetailsListUseCaseImpl implements GetRestaurantDetailsListUseCase {

    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final NearbyRepository nearbyRepository;
    @NonNull
    private final DetailsRepository detailsRepository;

    public GetRestaurantDetailsListUseCaseImpl(@NonNull LocationRepository locationRepository,
                                               @NonNull NearbyRepository nearbyRepository,
                                               @NonNull DetailsRepository detailsRepository
    ) {
        this.locationRepository = locationRepository;
        this.nearbyRepository = nearbyRepository;
        this.detailsRepository = detailsRepository;
    }

    @NonNull
    @Override
    public LiveData<ListModel> getDetailsList() {
        // 1. FETCH the current location
        return Transformations.switchMap(locationRepository.getCurrentLocation(), currentLocation -> {
            if (currentLocation != null) {

                // 2. FETCH the nearby restaurants to the current location (if not null)
                return Transformations.switchMap(nearbyRepository.getNearbyResponse(currentLocation), nearbyResponse -> {
                    final MediatorLiveData<ListModel> listModel = new MediatorLiveData<>();

                    if (nearbyResponse != null && nearbyResponse.getResults() != null) {
                        final List<DetailsResponse> currentDetailsList = new ArrayList<>();

                        for (int i = 0; i < nearbyResponse.getResults().size(); i++) {
                            final NearbyResponse.Result result = nearbyResponse.getResults().get(i);

                            // 3. FETCH the restaurant details with the place ID for each one of them
//                            final int finalI = i;
                            final LiveData<DetailsResponse> detailsResponseLiveData = detailsRepository.getDetailsResponse(result.getPlaceId());

                            listModel.addSource(detailsResponseLiveData, newDetailsResponse -> {

                                // 4. ADD the new details response to the list and UPDATE LiveData
                                currentDetailsList.add(newDetailsResponse);

//                                if (finalI == nearbyResponse.getResults().size() - 1) {
                                listModel.setValue(new ListModel(currentDetailsList, currentLocation));
//                                }

                                listModel.removeSource(detailsResponseLiveData);
                            });
                        }
                    }

                    return listModel;
                });
            } else {
                return new MediatorLiveData<>();
            }
        });
    }
}
