package com.radio.radioactive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {

    public static Notification createNotification(Context context, boolean playing, String artist, String title) {
        createChannel(context);

        Intent playPauseIntent = new Intent(context, RadioService.class);
        playPauseIntent.setAction(playing ? "ACTION_PAUSE" : "ACTION_PLAY");
        PendingIntent pendingIntent = PendingIntent.getService(
                context, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action action = new NotificationCompat.Action(
                playing ? R.drawable.ic_pause : R.drawable.ic_play,
                playing ? "Pause" : "Play",
                pendingIntent
        );

        return new NotificationCompat.Builder(context, "radio_channel")
                .setContentTitle(artist)
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_radio)
                .addAction(action)
                .setOngoing(playing)
                .build();
    }

    public static void updateNotification(Context context, boolean playing, String artist, String title) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, createNotification(context, playing, artist, title));
    }

    private static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "radio_channel", "Radio Playback", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}

