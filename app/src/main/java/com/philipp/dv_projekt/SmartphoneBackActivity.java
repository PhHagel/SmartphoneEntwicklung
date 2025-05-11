package com.philipp.dv_projekt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SmartphoneBackActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartphone_back);

        AudioPlayerHelper.playAudio(this, R.raw.smartphonezurueklegen, () -> {
            new android.os.Handler(getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(this, StandbyActivity.class));
                finish();
            }, DELAY_MILLIS);
        }, false);
    }
}
