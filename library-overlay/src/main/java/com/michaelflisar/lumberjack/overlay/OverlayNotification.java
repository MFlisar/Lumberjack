package com.michaelflisar.lumberjack.overlay;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.michaelflisar.lumberjack.OverlayLoggingSetup;
import com.michaelflisar.lumberjack.overlay.OverlayService;

/**
 * Created by Michael on 28.02.2017.
 */

public class OverlayNotification
{
    public static void show(Context context, OverlayLoggingSetup setup)
    {
        Intent notificationIntent = new Intent(context, OverlayService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0 /*request code*/, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(setup.getNotificationTitle())
                .setContentText(setup.getNotificationText())
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        builder.setSmallIcon(android.R.drawable.ic_media_pause);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(setup.getNotificationId(), builder.build());
    }
}
