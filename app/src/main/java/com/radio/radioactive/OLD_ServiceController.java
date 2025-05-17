package com.radio.radioactive;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


public class OLD_ServiceController {
/*
    private final Context context;
    private RadioService radioService;
    private boolean bound = false;
    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            RadioService.LocalBinder binder = (RadioService.LocalBinder) service;
            radioService = binder.getService();
            bound = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public OLD_ServiceController(Context context) {
        this.context = context;
    }

    public void bindService() {
        Intent intent = new Intent(context, RadioService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        if (bound) {
            context.unbindService(connection);
            bound = false;
        }
    }

    public void play() {
        if (bound) radioService.playStream();
        else context.startService(new Intent(context, RadioService.class));
    }

    public void pause() {
        if (bound) radioService.pauseStream();
    }*/
}
