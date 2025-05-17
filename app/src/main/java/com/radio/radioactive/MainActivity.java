package com.radio.radioactive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MainActivity extends AppCompatActivity {

    private Button playButton, pauseButton;
    private SeekBar volumeSeekBar;
    private TextView artistTextView, titleTextView;
    private ImageView thumbImageView;
    private AudioManager audioManager;
    private BroadcastReceiver songInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String artist = intent.getStringExtra("artist");
            String title = intent.getStringExtra("title");
            String thumbUrl = intent.getStringExtra("thumb_url");
            artistTextView.setText(artist);
            titleTextView.setText(title);
            Log.d("MainActivity", "thumb_url = " + thumbUrl);
            // Load image using Glide
            if (thumbUrl != null && !thumbUrl.isEmpty()) {
            Glide.with(context)
                    .load(thumbUrl)
                    .placeholder(R.drawable.placeholder_image) // Optional placeholder
                    .error(R.drawable.error_image) // Optional error image
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(thumbImageView);
            } else {
                thumbImageView.setImageResource(R.drawable.placeholder_image);
            }

        }
    };


    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // ✅ super must come first
        setContentView(R.layout.activity_main); // ✅ set layout before accessing views
        requestPermissions();

        playButton = findViewById(R.id.btnPlay);
        pauseButton = findViewById(R.id.btnPause);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        artistTextView = findViewById(R.id.txtArtist);
        titleTextView = findViewById(R.id.txtTitle);
        thumbImageView = findViewById(R.id.imgThumb);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        playButton.setOnClickListener(v -> startRadioService(true));
        pauseButton.setOnClickListener(v -> startRadioService(false));
    }

    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.radio.UPDATE_SONG_INFO");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(songInfoReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(songInfoReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(songInfoReceiver);
    }

    private void startRadioService(boolean play) {
        Intent intent = new Intent(this, RadioService.class);
        intent.setAction(play ? "ACTION_PLAY" : "ACTION_PAUSE");
        ContextCompat.startForegroundService(this, intent);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(songInfoReceiver);
    }

}
