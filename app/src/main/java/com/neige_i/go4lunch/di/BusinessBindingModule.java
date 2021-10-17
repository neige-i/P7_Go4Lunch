package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCase;
import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCaseImpl;
import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCase;
import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCaseImpl;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCaseImpl;
import com.neige_i.go4lunch.domain.firestore.GetNearbyFirestoreRestaurantsUseCase;
import com.neige_i.go4lunch.domain.firestore.GetNearbyFirestoreRestaurantsUseCaseImpl;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantDetailsUseCase;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantDetailsUseCaseImpl;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantsUseCase;
import com.neige_i.go4lunch.domain.google_places.GetNearbyRestaurantsUseCaseImpl;
import com.neige_i.go4lunch.domain.google_places.GetSingleRestaurantDetailsUseCase;
import com.neige_i.go4lunch.domain.google_places.GetSingleRestaurantDetailsUseCaseImpl;
import com.neige_i.go4lunch.domain.gps.GetGpsStatusUseCase;
import com.neige_i.go4lunch.domain.gps.GetGpsStatusUseCaseImpl;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.gps.RequestGpsUseCaseImpl;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCase;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCaseImpl;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCaseImpl;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCaseImpl;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCaseImpl;
import com.neige_i.go4lunch.domain.location.GetLocationUseCase;
import com.neige_i.go4lunch.domain.location.GetLocationUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.GetFirestoreUserListUseCase;
import com.neige_i.go4lunch.domain.to_sort.GetFirestoreUserListUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.ToggleFavRestaurantUseCase;
import com.neige_i.go4lunch.domain.to_sort.ToggleFavRestaurantUseCaseImpl;
import com.neige_i.go4lunch.domain.to_sort.UpdateSelectedRestaurantUseCase;
import com.neige_i.go4lunch.domain.to_sort.UpdateSelectedRestaurantUseCaseImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public abstract class BusinessBindingModule {

    // ---------------------------------------- DISPATCHER -----------------------------------------

    @Binds
    public abstract GetAuthUseCase getAuthUseCase(
        GetAuthUseCaseImpl getAuthUseCaseImpl
    );

    // ------------------------------------------- AUTH --------------------------------------------

    @Binds
    public abstract SignInAndUpdateDatabaseUseCase signInAndUpdateDatabaseUseCase(
        SignInAndUpdateDatabaseUseCaseImpl signInAndUpdateDatabaseUseCaseImpl
    );

    // ------------------------------------------- HOME --------------------------------------------

    @Binds
    public abstract FreeResourcesUseCase freeResourcesUseCase(
        FreeResourcesUseCaseImpl freeResourcesUseCaseImpl
    );

    @Binds
    public abstract GetLocationPermissionUseCase bindGetLocationPermissionUseCase(
        GetLocationPermissionUseCaseImpl getLocationPermissionUseCaseImpl
    );

    @Binds
    public abstract SetLocationUpdatesUseCase bindSetLocationUpdatesUseCase(
        SetLocationUpdatesUseCaseImpl setLocationUpdatesUseCaseImpl
    );

    @Binds
    public abstract ShowGpsDialogUseCase bindShowGpsDialogUseCase(
        ShowGpsDialogUseCaseImpl showGpsDialogUseCaseImpl
    );

    @Binds
    public abstract GetFirebaseUserUseCase bindGetFirebaseUserUseCase(
        GetFirebaseUserUseCaseImpl getFirebaseUserUseCaseImpl
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
    public abstract GetLocationUseCase bindGetLocationUseCase(
        GetLocationUseCaseImpl getLocationUseCaseImpl
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
    public abstract GetSingleRestaurantDetailsUseCase bindGetRestaurantDetailsItemUseCase(
        GetSingleRestaurantDetailsUseCaseImpl getRestaurantDetailsItemUseCaseImpl
    );

    @Binds
    public abstract GetNearbyRestaurantDetailsUseCase bindGetRestaurantDetailsListUseCase(
        GetNearbyRestaurantDetailsUseCaseImpl getRestaurantDetailsListUseCaseImpl
    );

    @Binds
    public abstract GetNearbyFirestoreRestaurantsUseCase bindGetFirestoreRestaurantsByIdUseCase(
        GetNearbyFirestoreRestaurantsUseCaseImpl getFirestoreRestaurantsByIdUseCaseImpl
    );

    @Binds
    public abstract ToggleFavRestaurantUseCase bindToggleFavRestaurantUseCase(
        ToggleFavRestaurantUseCaseImpl toggleFavRestaurantUseCaseImpl
    );

//    @Binds
//    public abstract UpdateInterestedWorkmatesUseCase bindUpdateInterestedWorkmatesUseCase(
//        UpdateInterestedWorkmatesUseCaseImpl updateInterestedWorkmatesUseCaseImpl
//    );

    @Binds
    public abstract UpdateSelectedRestaurantUseCase bindUpdateSelectedRestaurantUseCase(
        UpdateSelectedRestaurantUseCaseImpl updateSelectedRestaurantUseCaseImpl
    );
}
