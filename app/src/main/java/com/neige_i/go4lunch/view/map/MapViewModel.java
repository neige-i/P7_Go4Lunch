package com.neige_i.go4lunch.view.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.google_places.PlacesRepository;
import com.neige_i.go4lunch.data.google_places.model.NearbyResponse;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private MediatorLiveData<List<MapViewState>> mapViewStateMediatorLiveData = new MediatorLiveData<>();

    public MapViewModel(@NonNull PlacesRepository placesRepository) {

        LiveData<NearbyResponse> nearbyRestaurantsLiveData = placesRepository.getNearbyRestaurants();

        mapViewStateMediatorLiveData.addSource(nearbyRestaurantsLiveData, new Observer<NearbyResponse>() {
            @Override
            public void onChanged(NearbyResponse nearbyResponse) {
                combine(nearbyResponse);
            }
        });
    }

    public LiveData<List<MapViewState>> getMapViewStateLiveData() {
        return mapViewStateMediatorLiveData;
    }

    private void combine(@Nullable NearbyResponse nearbyResponse) {
        if (nearbyResponse != null) {
            map(nearbyResponse);
        }
    }

    private void map(@NonNull NearbyResponse nearbyResponse) {
        if (nearbyResponse.getResults() != null) {
            List<MapViewState> viewStates = new ArrayList<>();

            for (NearbyResponse.Result result : nearbyResponse.getResults()) {
                viewStates.add(
                    new MapViewState(
                        result.getPlaceId(),
                        result.getName()
                    )
                );
            }

            mapViewStateMediatorLiveData.setValue(viewStates);
        }
    }
}
