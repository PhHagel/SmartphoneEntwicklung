package com.philipp.dv_projekt;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TimeoutService extends Service {

    private static final long TIMEOUT_IN_MILLIS = 30_000;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;


    private final BroadcastReceiver resetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            restartTimeout();
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        timeoutHandler = new Handler(Looper.getMainLooper());

        timeoutRunnable = () -> {
            Intent splash = new Intent(TimeoutService.this, StandbyActivity.class);
            splash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(splash);
            stopSelf();
        };

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(resetReceiver, new IntentFilter("com.philipp.ACTION_RESET_TIMEOUT"));

        restartTimeout();
    }


    private void restartTimeout() {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_IN_MILLIS);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_IN_MILLIS);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacks(timeoutRunnable);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(resetReceiver);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}