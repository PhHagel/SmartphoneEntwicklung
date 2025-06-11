package com.philipp.dv_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SmartphoneBackActivity extends AppCompatActivity implements WebSocketCallback {

    private final Handler audioHandler = new Handler(Looper.getMainLooper());
    private final Runnable playAudioRunnable = new Runnable() {
        @Override
        public void run() {
            AudioPlayerHelper.playAudio(SmartphoneBackActivity.this, R.raw.smartphonezurueklegen, null);
            audioHandler.postDelayed(this, 20000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartphone_back);
        audioHandler.post(playAudioRunnable);

        WebSocketManager.getInstance().setCallback(this);
    }


    @Override
    public void onMessageReceived(String jsonText) {
        ServerResponseHandler handler = new ServerResponseHandler();
        ResponseResult result = handler.getResponseType(jsonText);

        runOnUiThread(() -> {
            if (AudioPlayerHelper.isPlaying()) {
                AudioPlayerHelper.setOnCompletionListener(mp -> handleMessageResponse(result));
            } else {
                handleMessageResponse(result);
            }
        });
    }


    private void handleMessageResponse(ResponseResult result) {
        switch (result.getType()) {

            case PHONE_IS_BACK:
                startActivity(new Intent(this, StandbyActivity.class));
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
    }


    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("MainActivity", "📨 Systemnachricht: " + systemText);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioHandler.removeCallbacks(playAudioRunnable);
    }

}
