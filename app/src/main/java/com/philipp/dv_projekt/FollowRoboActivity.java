package com.philipp.dv_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class FollowRoboActivity extends AppCompatActivity implements WebSocketCallback {

    private LottieAnimationView robotAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_robo);

        robotAnimation = findViewById(R.id.robotAnimation);
        robotAnimation.playAnimation();
        robotAnimation.setRepeatCount(Konstanten.LOTTY_REPEAT_COUNT);

        WebSocketManager.getInstance().setCallback(this);

        AudioPlayerHelper.playAudio(this, R.raw.roboterfolgen, null);
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

            case FAILURE:
                Toast.makeText(this, "Fehler: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                break;

            case ROBOT_REACHED_GOAL:
                robotAnimation.cancelAnimation();
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
