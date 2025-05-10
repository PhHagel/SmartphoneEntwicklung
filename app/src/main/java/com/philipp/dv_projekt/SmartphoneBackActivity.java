package com.philipp.dv_projekt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SmartphoneBackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartphone_back);

        AudioPlayerHelper.playAudio(this, R.raw.smartphonezurueklegen, () -> {
            new android.os.Handler(getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(this, SplashActivity.class));
                finish();
            }, 5000);
        }, false);
    }
}
