package com.neige_i.go4lunch.background;

import static com.neige_i.go4lunch.view.detail.DetailActivity.EXTRA_PLACE_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.WorkerDelegate;
import com.neige_i.go4lunch.domain.notification.GetNotificationInfoUseCase;
import com.neige_i.go4lunch.domain.notification.NotificationInfo;
import com.neige_i.go4lunch.view.detail.DetailActivity;

import java.util.List;
import java.util.StringJoiner;

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
    @NonNull
    private final GetNotificationInfoUseCase getNotificationInfoUseCase;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @AssistedInject
    public NotifyTimeToEatWorker(
        @Assisted @NonNull Context context,
        @Assisted @NonNull WorkerParameters workerParams,
        @NonNull WorkerDelegate workerDelegate,
        @NonNull GetNotificationInfoUseCase getNotificationInfoUseCase
    ) {
        super(context, workerParams);
        this.workerDelegate = workerDelegate;
        this.getNotificationInfoUseCase = getNotificationInfoUseCase;
    }

    // -------------------------------------- WORKER METHODS ---------------------------------------

    @NonNull
    @Override
    public Result doWork() {
        // Periodic work requests cannot be precise enough to run at a specific time
        // Instead, use a one-time work request and enqueue a new one once the previous has ended
        workerDelegate.enqueueOneTimeWorkRequest(getApplicationContext());

        sendNotification(getNotificationInfoUseCase.get());

        return Result.success();
    }

    private void sendNotification(@Nullable NotificationInfo notificationInfo) {
        if (notificationInfo == null) {
            return;
        }

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // NotificationChannel are not supported below API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(
                CHANNEL_ID,
                getApplicationContext().getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ));
        }

        // Setup PendingIntent
        final Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra(EXTRA_PLACE_ID, notificationInfo.getRestaurantId());
        final PendingIntent pendingIntent = TaskStackBuilder.create(getApplicationContext())
            .addNextIntentWithParentStack(detailIntent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup content and big text
        final String contentText = getContentText(notificationInfo);
        final String bigText = getBigText(notificationInfo.getWorkmateNames());

        // Display notification
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
    }

    @NonNull
    private String getContentText(@NonNull NotificationInfo notificationInfo) {
        return notificationInfo.getRestaurantName() +
            " - " +
            notificationInfo.getRestaurantAddress();
    }

    private String getBigText(@NonNull List<String> workmateNames) {
        final String bigText;

        if (workmateNames.isEmpty()) {
            bigText = getApplicationContext().getString(R.string.notification_big_text_without_workmates);
        } else {
            bigText = getApplicationContext().getString(
                R.string.notification_big_text_with_workmates,
                toJoinedString(workmateNames)
            );
        }
        return bigText;
    }

    @NonNull
    private String toJoinedString(@NonNull List<String> stringList) {
        final StringJoiner joiner = new StringJoiner(", ");

        for (String string : stringList) {
            joiner.add(string);
        }

        return joiner.toString();
    }
}
