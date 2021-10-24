package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCase;
import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCaseImpl;
import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCase;
import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCaseImpl;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCase;
import com.neige_i.go4lunch.domain.firebase.GetFirebaseUserUseCaseImpl;
import com.neige_i.go4lunch.domain.google_places.GetSingleRestaurantDetailsUseCase;
import com.neige_i.go4lunch.domain.google_places.GetSingleRestaurantDetailsUseCaseImpl;
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
import com.neige_i.go4lunch.domain.list_restaurant.GetNearbyDetailsUseCase;
import com.neige_i.go4lunch.domain.list_restaurant.GetNearbyDetailsUseCaseImpl;
import com.neige_i.go4lunch.domain.list_workmate.GetAllWorkmatesUseCase;
import com.neige_i.go4lunch.domain.list_workmate.GetAllWorkmatesUseCaseImpl;
import com.neige_i.go4lunch.domain.map.GetMapDataUseCase;
import com.neige_i.go4lunch.domain.map.GetMapDataUseCaseImpl;
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

    // -------------------------------------------- MAP --------------------------------------------

    @Binds
    public abstract GetMapDataUseCase bindGetMapDataUseCase(
        GetMapDataUseCaseImpl getMapDataUseCaseImpl
    );

    // -------------------------------------- RESTAURANT LIST --------------------------------------

    @Binds
    public abstract GetNearbyDetailsUseCase bindGetNearbyDetailsUseCase(
        GetNearbyDetailsUseCaseImpl getNearbyDetailsUseCaseImpl
    );

    // --------------------------------------- WORKMATE LIST ---------------------------------------

    @Binds
    public abstract GetAllWorkmatesUseCase bindGetFirestoreUserListUseCase(
        GetAllWorkmatesUseCaseImpl getFirestoreUserListUseCaseImpl
    );

    @Binds
    public abstract GetFirebaseUserUseCase bindGetFirebaseUserUseCase(
        GetFirebaseUserUseCaseImpl getFirebaseUserUseCaseImpl
    );

    @Binds
    public abstract RequestGpsUseCase bindRequestGpsUseCase(
        RequestGpsUseCaseImpl requestGpsUseCaseImpl
    );

    @Binds
    public abstract GetSingleRestaurantDetailsUseCase bindGetRestaurantDetailsItemUseCase(
        GetSingleRestaurantDetailsUseCaseImpl getRestaurantDetailsItemUseCaseImpl
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
