package com.philipp.dv_projekt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class FollowRoboActivity extends AppCompatActivity {

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_robo);

        // Audio abspielen
        if (player == null) {
            player = MediaPlayer.create(FollowRoboActivity.this, R.raw.bildaufnehmen);

            // Setze OnCompletionListener, um auf das Ende des Audios zu warten
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // Wenn das Audio zu Ende ist, starte die nächste Aktivität
                    Intent intentSmartphoneBackActivity = new Intent(FollowRoboActivity.this, SmartphoneBackActivity.class);
                    startActivity(intentSmartphoneBackActivity);
                    finish();
                }
            });

            player.start();
        }
    }
}
