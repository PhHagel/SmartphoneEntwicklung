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


    private static final long TIMEOUT_IN_MILLIS = 30_000;  // 30 Sekunden später
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
        // Handler auf dem Main–Looper
        timeoutHandler = new Handler(Looper.getMainLooper());

        // Runnable, das bei Timeout die SplashActivity startet
        timeoutRunnable = () -> {
            Intent splash = new Intent(TimeoutService.this, StandbyActivity.class);
            splash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(splash);
            stopSelf();  // Service beenden, falls gewünscht
        };

        // Receiver registrieren
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(resetReceiver, new IntentFilter("com.philipp.ACTION_RESET_TIMEOUT"));

        // Erstmal Timeout starten
        restartTimeout();
    }


    private void restartTimeout() {
        // ggf. vorhandenes Runnable abbrechen
        timeoutHandler.removeCallbacks(timeoutRunnable);
        // neuen Countdown starten
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_IN_MILLIS);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_IN_MILLIS);
        // Service im Hintergrund weiterlaufen lassen
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Aufräumen
        timeoutHandler.removeCallbacks(timeoutRunnable);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(resetReceiver);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null; // Nicht gebunden
    }


}