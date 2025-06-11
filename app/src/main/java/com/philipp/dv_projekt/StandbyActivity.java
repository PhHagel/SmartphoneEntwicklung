package com.philipp.dv_projekt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class StandbyActivity extends AppCompatActivity implements SensorEventListener, WebSocketCallback {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean switched = false;
    private Vibrator vibrator;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        if (vibratorManager != null) {
            vibrator = vibratorManager.getDefaultVibrator();
        }

        handler = new Handler(Looper.getMainLooper());

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (switched) return;

        float y = event.values[1];
        float z = event.values[2];

        if (z < 7 && y > 3) {
            switched = true;

            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }

            handler.postDelayed(() -> {
                startActivity(new Intent(StandbyActivity.this, MainActivity.class));
                finish();
            }, 300);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    @Override
    public void onMessageReceived(String jsonText) {

        runOnUiThread(() -> {

            ServerResponseHandler handler = new ServerResponseHandler();
            ResponseResult result = handler.getResponseType(jsonText);

            switch (result.getType()) {

                case AUDIO_GENERATION_REQUEST_FAILURE:
                    Toast.makeText(this, "Fehler in AUDIO_GENERATION_REQUEST: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;

                case AUDIO_GENERATION_REQUEST_SUCCESS:
                    startActivity(new Intent(this, AudioPlayActivity.class));
                    finish();
                    break;

                case FAILURE:
                    Toast.makeText(this, "Fehler: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;

                case UNKNOWN_RESPONSE:
                    Toast.makeText(this, "Unknown Response: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(this, "Fehler in der implementierung!!!", Toast.LENGTH_SHORT).show();
                    break;

            }

        });

    }


    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("SplashActivity", "📨 Systemnachricht: " + systemText);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

}