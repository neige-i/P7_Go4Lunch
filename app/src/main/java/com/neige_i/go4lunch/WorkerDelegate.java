package com.neige_i.go4lunch;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.neige_i.go4lunch.background.NotifyTimeToEatWorker;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class WorkerDelegate {

    @NonNull
    private final Clock clock;

    @Inject
    public WorkerDelegate(@NonNull Clock clock) {
        this.clock = clock;
    }

    public void enqueueOneTimeWorkRequest(@NonNull Context context) {
        // Compute time difference to set the initial delay
        Duration timeDiff = Duration.between(LocalTime.now(clock), LocalTime.of(12, 0));
        if (timeDiff.isNegative()) {
            timeDiff = timeDiff.plusDays(1);
        }

        // Enqueue the work request
        WorkManager.getInstance(context).enqueue(
            new OneTimeWorkRequest.Builder(NotifyTimeToEatWorker.class)
                .setInitialDelay(timeDiff.toMillis(), TimeUnit.MILLISECONDS)
                .build()
        );
    }
}
