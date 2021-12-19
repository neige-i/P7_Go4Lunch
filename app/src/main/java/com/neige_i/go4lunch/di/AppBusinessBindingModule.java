package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.domain.notification.GetNotificationInfoUseCase;
import com.neige_i.go4lunch.domain.notification.GetNotificationInfoUseCaseImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AppBusinessBindingModule {

    // --------------------------------------- NOTIFICATION ----------------------------------------

    @Binds
    public abstract GetNotificationInfoUseCase bindGetNotificationInfoUseCase(
        GetNotificationInfoUseCaseImpl getNotificationInfoUseCaseImpl
    );
}
