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

        if (player == null) {
            player = MediaPlayer.create(FollowRoboActivity.this, R.raw.roboterfolgen);

            // hier muss noch abgehandelt werden, dass der Roboter am Ziel ist




            player.setOnCompletionListener(mediaPlayer -> {
                Intent intentSmartphoneBackActivity = new Intent(FollowRoboActivity.this, SmartphoneBackActivity.class);
                startActivity(intentSmartphoneBackActivity);
                finish();
            });

            player.start();
        }
    }
}
