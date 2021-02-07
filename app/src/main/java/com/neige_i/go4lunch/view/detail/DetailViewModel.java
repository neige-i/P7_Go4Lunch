package com.neige_i.go4lunch.view.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.google_places.PlacesRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;

public class DetailViewModel extends ViewModel {

    private final PlacesRepository detailsRepository;

    public DetailViewModel(PlacesRepository detailsRepository) {
        this.detailsRepository = detailsRepository;
    }

    public LiveData<DetailViewState> getViewState(@NonNull String placeId) {
        return Transformations.map(detailsRepository.getPlacesResponse(placeId), detailsResponse -> {
            final DetailsResponse.Result result = ((DetailsResponse) detailsResponse).getResult();

            // Shorten the address: "2 Rue du Vivienne, 75005 Paris, France" -> "2 Rue du Vivienne"
            final String shortAddress = result.getFormattedAddress().substring(0, result.getFormattedAddress().indexOf(','));

            // Change rating: Google [1.0,5.0] -> (-1) -> [0.0,4.0] -> (*.75) -> [0.0,3.0] -> (round) -> [0,3] Go4Lunch
            final int rating = (int) Math.round((result.getRating() - 1) * .75);

            // TODO: handle empty field case
            return new DetailViewState(
                result.getPlaceId(),
                result.getName(),
                "",
                shortAddress,
                rating,
                result.getFormattedPhoneNumber(),
                result.getWebsite(),
                false,
                false
            );
        });
    }
}
