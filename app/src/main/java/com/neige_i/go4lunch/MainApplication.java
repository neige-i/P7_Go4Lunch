package com.neige_i.go4lunch;

import android.app.Application;

public class MainApplication extends Application {

    private static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }
}
