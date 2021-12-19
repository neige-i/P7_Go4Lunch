package com.neige_i.go4lunch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application implements Configuration.Provider {

    @Inject
    WorkerDelegate workerDelegate;
    @Inject
    HiltWorkerFactory hiltWorkerFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        workerDelegate.enqueueOneTimeWorkRequest(this);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build();
    }
}
