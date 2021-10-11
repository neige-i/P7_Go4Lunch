package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCaseImpl;
import com.neige_i.go4lunch.domain.firestore.CreateFirestoreUserUseCase;
import com.neige_i.go4lunch.domain.firestore.CreateFirestoreUserUseCaseImpl;
import com.neige_i.go4lunch.domain.firestore.GetFirestoreUserUseCase;
import com.neige_i.go4lunch.domain.firestore.GetFirestoreUserUseCaseImpl;
import com.neige_i.go4lunch.domain.gps.GetGpsStatusUseCase;
import com.neige_i.go4lunch.domain.gps.GetGpsStatusUseCaseImpl;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCaseImpl;
import com.neige_i.go4lunch.domain.gps.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.gps.ShowGpsDialogUseCaseImpl;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationPermissionUseCaseImpl;
import com.neige_i.go4lunch.domain.location.GetLocationUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationUseCaseImpl;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.location.SetLocationUpdatesUseCaseImpl;
import com.neige_i.go4lunch.domain.place_nearby.GetNearbyRestaurantsUseCase;
import com.neige_i.go4lunch.domain.place_nearby.GetNearbyRestaurantsUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.GetFirestoreUserListUseCase;
import com.neige_i.go4lunch.domain.to_sort.GetFirestoreUserListUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsItemUseCase;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsItemUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsListUseCase;
import com.neige_i.go4lunch.domain.to_sort.GetRestaurantDetailsListUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.ToggleFavRestaurantUseCase;
import com.neige_i.go4lunch.domain.to_sort.ToggleFavRestaurantUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.UpdateInterestedWorkmatesUseCase;
import com.neige_i.go4lunch.domain.to_sort.UpdateInterestedWorkmatesUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.UpdateSelectedRestaurantUseCase;
import com.neige_i.go4lunch.domain.to_sort.UpdateSelectedRestaurantUseCaseImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class) // ASKME: tips for how to group module's functions
public abstract class UseCaseModule {

    @Binds
    public abstract GetFirebaseUserUseCase bindGetFirebaseUserUseCase(
        GetFirebaseUserUseCaseImpl getFirebaseUserUseCaseImpl
    );

    @Binds
    public abstract GetFirestoreUserUseCase bindGetFirestoreUserUseCase(
        GetFirestoreUserUseCaseImpl getFirestoreUserUseCaseImpl
    );

    @Binds
    public abstract CreateFirestoreUserUseCase bindCreateFirestoreUserUseCase(
        CreateFirestoreUserUseCaseImpl createFirestoreUserUseCaseImpl
    );

    @Binds
    public abstract GetGpsStatusUseCase bindGetGpsStatusUseCase(
        GetGpsStatusUseCaseImpl getGpsStatusUseCaseImpl
    );

    @Binds
    public abstract RequestGpsUseCase bindRequestGpsUseCase(
        RequestGpsUseCaseImpl requestGpsUseCaseImpl
    );

    @Binds
    public abstract ShowGpsDialogUseCase bindShowGpsDialogUseCase(
        ShowGpsDialogUseCaseImpl showGpsDialogUseCaseImpl
    );

    @Binds
    public abstract GetLocationPermissionUseCase bindGetLocationPermissionUseCase(
        GetLocationPermissionUseCaseImpl getLocationPermissionUseCaseImpl
    );

    @Binds
    public abstract GetLocationUseCase bindGetLocationUseCase(
        GetLocationUseCaseImpl getLocationUseCaseImpl
    );

    @Binds
    public abstract SetLocationUpdatesUseCase bindSetLocationUpdatesUseCase(
        SetLocationUpdatesUseCaseImpl setLocationUpdatesUseCaseImpl
    );

    @Binds
    public abstract GetNearbyRestaurantsUseCase bindGetNearbyRestaurantsUseCase(
        GetNearbyRestaurantsUseCaseImpl getNearbyRestaurantsUseCaseImpl
    );

    @Binds
    public abstract GetFirestoreUserListUseCase bindGetFirestoreUserListUseCase(
        GetFirestoreUserListUseCaseImpl getFirestoreUserListUseCaseImpl
    );

    @Binds
    public abstract GetRestaurantDetailsItemUseCase bindGetRestaurantDetailsItemUseCase(
        GetRestaurantDetailsItemUseCaseImpl getRestaurantDetailsItemUseCaseImpl
    );

    @Binds
    public abstract GetRestaurantDetailsListUseCase bindGetRestaurantDetailsListUseCase(
        GetRestaurantDetailsListUseCaseImpl getRestaurantDetailsListUseCaseImpl
    );

    @Binds
    public abstract ToggleFavRestaurantUseCase bindToggleFavRestaurantUseCase(
        ToggleFavRestaurantUseCaseImpl toggleFavRestaurantUseCaseImpl
    );

    @Binds
    public abstract UpdateInterestedWorkmatesUseCase bindUpdateInterestedWorkmatesUseCase(
        UpdateInterestedWorkmatesUseCaseImpl updateInterestedWorkmatesUseCaseImpl
    );

    @Binds
    public abstract UpdateSelectedRestaurantUseCase bindUpdateSelectedRestaurantUseCase(
        UpdateSelectedRestaurantUseCaseImpl updateSelectedRestaurantUseCaseImpl
    );
}
