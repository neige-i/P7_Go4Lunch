package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCase;
import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCaseImpl;
import com.neige_i.go4lunch.domain.chat.AddMessageUseCase;
import com.neige_i.go4lunch.domain.chat.AddMessageUseCaseImpl;
import com.neige_i.go4lunch.domain.chat.GetChatInfoUseCase;
import com.neige_i.go4lunch.domain.chat.GetChatInfoUseCaseImpl;
import com.neige_i.go4lunch.domain.detail.GetRestaurantInfoUseCase;
import com.neige_i.go4lunch.domain.detail.GetRestaurantInfoUseCaseImpl;
import com.neige_i.go4lunch.domain.detail.UpdateRestaurantPrefUseCase;
import com.neige_i.go4lunch.domain.detail.UpdateRestaurantPrefUseCaseImpl;
import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCase;
import com.neige_i.go4lunch.domain.dispatcher.GetAuthUseCaseImpl;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCase;
import com.neige_i.go4lunch.domain.home.FreeResourcesUseCaseImpl;
import com.neige_i.go4lunch.domain.home.GetAutocompleteResultsUseCase;
import com.neige_i.go4lunch.domain.home.GetAutocompleteResultsUseCaseImpl;
import com.neige_i.go4lunch.domain.home.GetDrawerInfoUseCase;
import com.neige_i.go4lunch.domain.home.GetDrawerInfoUseCaseImpl;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCase;
import com.neige_i.go4lunch.domain.home.GetLocationPermissionUseCaseImpl;
import com.neige_i.go4lunch.domain.home.LogoutUseCase;
import com.neige_i.go4lunch.domain.home.LogoutUseCaseImpl;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCase;
import com.neige_i.go4lunch.domain.home.SetLocationUpdatesUseCaseImpl;
import com.neige_i.go4lunch.domain.home.SetSearchQueryUseCase;
import com.neige_i.go4lunch.domain.home.SetSearchQueryUseCaseImpl;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCase;
import com.neige_i.go4lunch.domain.home.ShowGpsDialogUseCaseImpl;
import com.neige_i.go4lunch.domain.list_restaurant.GetNearbyDetailsUseCase;
import com.neige_i.go4lunch.domain.list_restaurant.GetNearbyDetailsUseCaseImpl;
import com.neige_i.go4lunch.domain.list_workmate.GetAllWorkmatesUseCase;
import com.neige_i.go4lunch.domain.list_workmate.GetAllWorkmatesUseCaseImpl;
import com.neige_i.go4lunch.domain.map.GetMapDataUseCase;
import com.neige_i.go4lunch.domain.map.GetMapDataUseCaseImpl;
import com.neige_i.go4lunch.domain.map.RequestGpsUseCase;
import com.neige_i.go4lunch.domain.map.RequestGpsUseCaseImpl;
import com.neige_i.go4lunch.domain.settings.HandleSettingsPreferencesUseCase;
import com.neige_i.go4lunch.domain.settings.HandleSettingsPreferencesUseCaseImpl;

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
    public abstract GetAutocompleteResultsUseCase bindGetAutocompleteResultsUseCase(
        GetAutocompleteResultsUseCaseImpl getAutocompleteResultsUseCaseImpl
    );

    @Binds
    public abstract SetSearchQueryUseCase bindSetSearchQueryUseCase(
        SetSearchQueryUseCaseImpl setSearchQueryUseCaseImpl
    );

    @Binds
    public abstract GetDrawerInfoUseCase bindGetDrawerInfoUseCase(
        GetDrawerInfoUseCaseImpl getDrawerInfoUseCaseImpl
    );

    @Binds
    public abstract LogoutUseCase bindLogoutUseCase(
        LogoutUseCaseImpl logoutUseCaseImpl
    );

    // -------------------------------------------- MAP --------------------------------------------

    @Binds
    public abstract GetMapDataUseCase bindGetMapDataUseCase(
        GetMapDataUseCaseImpl getMapDataUseCaseImpl
    );

    @Binds
    public abstract RequestGpsUseCase bindRequestGpsUseCase(
        RequestGpsUseCaseImpl requestGpsUseCaseImpl
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

    // ------------------------------------------ DETAIL -------------------------------------------

    @Binds
    public abstract GetRestaurantInfoUseCase bindGetRestaurantInfoUseCase(
        GetRestaurantInfoUseCaseImpl getRestaurantInfoUseCaseImpl
    );

    @Binds
    public abstract UpdateRestaurantPrefUseCase bindUpdateRestaurantPrefUseCase(
        UpdateRestaurantPrefUseCaseImpl updateRestaurantPrefUseCaseImpl
    );

    // ------------------------------------------- CHAT --------------------------------------------

    @Binds
    public abstract GetChatInfoUseCase bindGetChatInfoUseCase(
        GetChatInfoUseCaseImpl getChatInfoUseCaseImpl
    );

    @Binds
    public abstract AddMessageUseCase bindAddMessageUseCase(
        AddMessageUseCaseImpl addMessageUseCaseImpl
    );

    // ----------------------------------------- SETTINGS ------------------------------------------

    @Binds
    public abstract HandleSettingsPreferencesUseCase bindHandleNotificationPreferencesUseCase(
        HandleSettingsPreferencesUseCaseImpl handleNotificationPreferencesUseCaseImpl
    );
}
