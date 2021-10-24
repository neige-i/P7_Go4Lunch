package com.neige_i.go4lunch.di;

import com.neige_i.go4lunch.view.ImageDelegate;
import com.neige_i.go4lunch.view.ImageDelegateImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public abstract class ViewBindingModule {

    @Binds
    public abstract ImageDelegate bindRatingImageDelegate(
        ImageDelegateImpl ratingImageDelegateImpl
    );
}
