package com.philipp.dv_projekt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;

public class FollowRoboActivity extends AppCompatActivity implements WebSocketCallback {

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_robo);

        WebSocketManager.getInstance().setCallback(this);

        if (player != null) {
            player.release();
            player = null;
        }
        player = MediaPlayer.create(this, R.raw.roboterfolgen);
        player.start();


        // das muss dann noch weg, wenn der Server die richtige antwort sendet !!!!!!!!
        player.setOnCompletionListener(mediaPlayer -> {
            Intent intentSmartphoneBackActivity = new Intent(FollowRoboActivity.this, SmartphoneBackActivity.class);
            startActivity(intentSmartphoneBackActivity);
            finish();
        });
    }

    @Override
    public void onMessageReceived(String jsonText) {
        runOnUiThread(() -> {
            if (player != null && player.isPlaying()) {
                // Noch am Abspielen -> warten bis es fertig ist
                player.setOnCompletionListener(mp -> handleMessageResponse(jsonText));
            } else {
                // Schon fertig -> direkt ausführen
                handleMessageResponse(jsonText);
            }
        });
    }

    private void handleMessageResponse(String jsonText) {
        ServerResponseHandler handler = new ServerResponseHandler();
        ResponseType type = handler.getResponseType(jsonText);

        if (Objects.requireNonNull(type) == ResponseType.ROBOT_REACHED_GOAL) {
            Intent intentSmartphoneBackActivity = new Intent(FollowRoboActivity.this, SmartphoneBackActivity.class);
            startActivity(intentSmartphoneBackActivity);
            finish();
        } else {
            Toast.makeText(this, "❓ Unbekannte Antwort vom Server", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("FollowRoboActivity", "📨 Systemnachricht: " + systemText);
    }
}
