package com.neige_i.go4lunch.background;

import static com.neige_i.go4lunch.view.detail.DetailActivity.EXTRA_PLACE_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.WorkerDelegate;
import com.neige_i.go4lunch.view.detail.DetailActivity;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class NotifyTimeToEatWorker extends Worker {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    @NonNull
    private static final String CHANNEL_ID = "CHANNEL_ID";

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final WorkerDelegate workerDelegate;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @AssistedInject
    public NotifyTimeToEatWorker(
        @Assisted @NonNull Context context,
        @Assisted @NonNull WorkerParameters workerParams,
        @NonNull WorkerDelegate workerDelegate
    ) {
        super(context, workerParams);
        this.workerDelegate = workerDelegate;
    }

    // -------------------------------------- WORKER METHODS ---------------------------------------

    @NonNull
    @Override
    public Result doWork() {
        // Periodic work requests cannot be precise enough to run at a specific time
        // Instead, use a one-time work request and enqueue a new one once the previous has ended
        workerDelegate.enqueueOneTimeWorkRequest(getApplicationContext());

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // Create the NotificationChannel for API 26+ only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(
                CHANNEL_ID,
                getApplicationContext().getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ));
        }

        // Setup PendingIntent
        final Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra(EXTRA_PLACE_ID, "ChIJEZSrfyRL5kcRmaFKQvhlpwg");
        final PendingIntent pendingIntent = TaskStackBuilder.create(getApplicationContext())
            .addNextIntentWithParentStack(detailIntent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Display notification
        final String contentText = "McDo - 10 rue des roses";
        final String bigText = getApplicationContext()
            .getString(R.string.notification_big_text_without_workmates);

        notificationManager.notify(
            0,
            new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getApplicationContext().getString(R.string.notification_title))
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText + "\n" + bigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        );

        return Result.success();
    }
}
