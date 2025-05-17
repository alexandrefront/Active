package com.radio.radioactive;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SongInfoFetcher {

    // Static variables to hold last known values
    private static String lastArtist = "";
    private static String lastTitle = "";

    public static void fetch(Context context) {
        new Thread(() -> {
            try {
                URL url = new URL("https://www.radio-active.net/stream/en_cours.json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) json.append(line);

                JSONObject obj = new JSONObject(json.toString());
                String artist = obj.optString("artist", "Unknown");
                String title = obj.optString("title", "Unknown");
                String thumbUrl = obj.optString("thumb_url", "");
                // Check if artist or title changed
                if (!artist.equals(lastArtist) || !title.equals(lastTitle)) {
                    lastArtist = artist;
                    lastTitle = title;

                    // Only update if new info is different
                    Intent updateIntent = new Intent("com.radio.UPDATE_SONG_INFO");
                    updateIntent.putExtra("artist", artist);
                    updateIntent.putExtra("title", title);
                    updateIntent.putExtra("thumb_url", thumbUrl);
                    context.sendBroadcast(updateIntent);

                    NotificationUtils.updateNotification(context, true, artist, title);
                }

                Intent updateIntent = new Intent("com.radio.UPDATE_SONG_INFO");
                updateIntent.putExtra("artist", artist);
                updateIntent.putExtra("title", title);
                context.sendBroadcast(updateIntent);

                NotificationUtils.updateNotification(context, true, artist, title);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

