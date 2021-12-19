package com.neige_i.go4lunch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.neige_i.go4lunch.background.NotifyTimeToEatWorker;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application implements Configuration.Provider {

    @Inject
    Clock clock;
    @Inject
    HiltWorkerFactory hiltWorkerFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        WorkManager.getInstance(this).enqueue(
            new PeriodicWorkRequest.Builder(NotifyTimeToEatWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(getTimeDiffToMiddayInMillis(), TimeUnit.MILLISECONDS)
                .build()
        );
    }

    private long getTimeDiffToMiddayInMillis() {
        Duration timeDiff = Duration.between(LocalTime.now(clock), LocalTime.of(12, 0));

        if (timeDiff.isNegative()) {
            timeDiff = timeDiff.plusDays(1);
        }

        return timeDiff.toMillis();
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build();
    }
}
