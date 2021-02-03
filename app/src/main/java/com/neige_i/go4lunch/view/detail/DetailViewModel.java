package com.neige_i.go4lunch.view.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.go4lunch.data.google_places.DetailsRepository;
import com.neige_i.go4lunch.data.google_places.model.DetailsResponse;

public class DetailViewModel extends ViewModel {

    private final DetailsRepository detailsRepository;

    public DetailViewModel(DetailsRepository detailsRepository) {
        this.detailsRepository = detailsRepository;
    }

    public LiveData<DetailViewState> getViewState(@NonNull String placeId) {
        return Transformations.map(detailsRepository.executeDetailsRequest(placeId), detailsResponse -> {
            final DetailsResponse.Result result = detailsResponse.getResult();

            // Shorten the address: "8 Rue du Fouarre, 75005 Paris, France" -> "8 Rue du Fouarre"
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
