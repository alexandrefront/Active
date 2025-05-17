package com.radio.radioactive;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class OLD_MediaNotificationManager {

    private final Context context;

    public OLD_MediaNotificationManager(Context context) {
        this.context = context;
    }

    public Notification buildNotification(boolean isPlaying) {
        String action = isPlaying ? "ACTION_PAUSE" : "ACTION_PLAY";
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                1,
                new Intent(context, RadioService.class).setAction(action),
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                isPlaying ? R.drawable.ic_pause : R.drawable.ic_play,
                isPlaying ? "Pause" : "Play",
                pendingIntent
        );

        return new NotificationCompat.Builder(context, "radio_channel")
                .setContentTitle("Radioactive")
                .setContentText("Playing Radio Stream")
                .setSmallIcon(R.drawable.ic_play)
                .addAction(playPauseAction)
                .setOnlyAlertOnce(true)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    public void updateNotification(boolean isPlaying) {
        NotificationManagerCompat.from(context).notify(1, buildNotification(isPlaying));
    }
}
