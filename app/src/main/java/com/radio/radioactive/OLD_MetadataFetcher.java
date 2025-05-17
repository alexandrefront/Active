package com.radio.radioactive;

import android.content.Context;
import android.content.Intent;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class OLD_MetadataFetcher extends Thread {

    private static final String STREAM_URL = "http://www.radio-active.net:8000/active";
    private final Context context;
    private volatile boolean running = true;

    public OLD_MetadataFetcher(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        while (running) {
            fetchMetadata();
            try {
                Thread.sleep(15000); // poll every 15s
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private void fetchMetadata() {
        try {
            URL url = new URL(STREAM_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Icy-MetaData", "1");
            conn.setRequestProperty("Connection", "close");
            conn.connect();

            int metaInt = 0;
            Map<String, List<String>> headers = conn.getHeaderFields();
            for (String key : headers.keySet()) {
                if ("icy-metaint".equalsIgnoreCase(key)) {
                    metaInt = Integer.parseInt(headers.get(key).get(0));
                    break;
                }
            }

            if (metaInt == 0) return;

            InputStream stream = conn.getInputStream();
            stream.skip(metaInt);
            int metaDataLength = stream.read() * 16;
            if (metaDataLength == 0) return;

            byte[] metaData = new byte[metaDataLength];
            stream.read(metaData, 0, metaDataLength);
            String metaString = new String(metaData);
            String title = parseStreamTitle(metaString);

            if (title != null) {
                Intent intent = new Intent("com.example.radioactive.METADATA_UPDATE");
                intent.putExtra("title", title);
                context.sendBroadcast(intent);
            }

            stream.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseStreamTitle(String metadata) {
        String titleKey = "StreamTitle='";
        int start = metadata.indexOf(titleKey);
        if (start == -1) return null;
        int end = metadata.indexOf("';", start);
        if (end == -1) return null;
        return metadata.substring(start + titleKey.length(), end);
    }

    public void stopFetching() {
        running = false;
        this.interrupt();
    }
}
