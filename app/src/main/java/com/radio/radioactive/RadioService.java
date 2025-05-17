package com.radio.radioactive;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

public class RadioService extends Service {

    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable songInfoUpdater;

    @Override
    public void onCreate() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        handler = new Handler(Looper.getMainLooper());
        songInfoUpdater = () -> {
            SongInfoFetcher.fetch(getApplicationContext());
            handler.postDelayed(songInfoUpdater, 100000);
        };
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if ("ACTION_PLAY".equals(action)) {
            playStream();
        } else if ("ACTION_PAUSE".equals(action)) {
            pauseStream();
        }
        return START_STICKY;
    }

    public void playStream() {
        try {
            if (mediaPlayer.isPlaying()) return;
            mediaPlayer.reset();

            String mediaUrl="https://stream.radio-active.net:8443/active";

            // Set audio attributes (preferred over deprecated setAudioStreamType)
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            mediaPlayer.setDataSource(mediaUrl);

            mediaPlayer.setOnPreparedListener(mp -> mp.start());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "Error occurred: " + what + ", " + extra);
                return true;
            });

            mediaPlayer.prepareAsync();

            startForeground(1, NotificationUtils.createNotification(this, true, "", ""));
            handler.post(songInfoUpdater);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseStream() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopForeground(false);
            NotificationUtils.updateNotification(this, false, "", "");
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        handler.removeCallbacks(songInfoUpdater);
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {return null;

    }
}

