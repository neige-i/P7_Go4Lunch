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
import com.neige_i.go4lunch.domain.notification.GetNotificationInfoUseCase;
import com.neige_i.go4lunch.domain.notification.NotificationInfo;
import com.neige_i.go4lunch.view.detail.DetailActivity;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class NotifyTimeToEatWorker extends Worker {

    @NonNull
    private static final String CHANNEL_ID = "CHANNEL_ID";

    @NonNull
    private final GetNotificationInfoUseCase getNotificationInfoUseCase;

    @AssistedInject
    public NotifyTimeToEatWorker(
        @Assisted @NonNull Context context,
        @Assisted @NonNull WorkerParameters workerParams,
        @NonNull GetNotificationInfoUseCase getNotificationInfoUseCase
    ) {
        super(context, workerParams);
        this.getNotificationInfoUseCase = getNotificationInfoUseCase;
    }

    @NonNull
    @Override
    public Result doWork() {
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

        // Setup PendingIntent with TaskStackBuilder
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

    private String getBigText(@NonNull String workmateNames) {
        if (workmateNames.isEmpty()) {
            return getApplicationContext().getString(R.string.notification_big_text_without_workmates);
        } else {
            return getApplicationContext().getString(
                R.string.notification_big_text_with_workmates,
                workmateNames
            );
        }
    }
}
