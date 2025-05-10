package com.philipp.dv_projekt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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


        // TODO wenn der Server die richtige Antwort zurückgibt (ROBOT_REACHED_GOAL), dann kann der Teil weg
        player.setOnCompletionListener(mediaPlayer -> {
            Intent intentSmartphoneBackActivity = new Intent(FollowRoboActivity.this, SmartphoneBackActivity.class);
            startActivity(intentSmartphoneBackActivity);
            finish();
        });
    }

    @Override
    public void onMessageReceived(String jsonText) {
        ServerResponseHandler handler = new ServerResponseHandler();
        ResponseResult result = handler.getResponseType(jsonText);

        runOnUiThread(() -> {
            if (player != null && player.isPlaying()) {
                // Noch am Abspielen -> warten bis es fertig ist
                player.setOnCompletionListener(mp -> handleMessageResponse(result));
            } else {
                // Schon fertig -> direkt ausführen
                handleMessageResponse(result);
            }
        });
    }

    private void handleMessageResponse(ResponseResult result) {

        switch (result.getType()) {

            case FAILURE:
                Toast.makeText(this, "Fehler: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                break;

            case ROBOT_REACHED_GOAL:
                startActivity(new Intent(this, SmartphoneBackActivity.class));
                finish();
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
        Log.d("FollowRoboActivity", "📨 Systemnachricht: " + systemText);
    }
}
