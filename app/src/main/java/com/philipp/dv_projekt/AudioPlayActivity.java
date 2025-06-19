package com.philipp.dv_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayActivity extends AppCompatActivity implements WebSocketCallback {

    private File localAudioFile;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private LottieAnimationView robotAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);
        WebSocketManager.getInstance().setCallback(this);
        robotAnimation = findViewById(R.id.robotAnimation);
        robotAnimation.playAnimation();
        robotAnimation.setRepeatCount(Konstanten.LOTTY_REPEAT_COUNT);
        downloadAndPlayAudio();
    }


    private void downloadAndPlayAudio() {
        executor.execute(() -> {
            boolean success = false;
            try {
                URL url = new URL(Konstanten.DOWNLOAD_SPRACHE_URL);
                Log.d("AudioDownload", "✅ Downloading audio from: " + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();


                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    localAudioFile = new File(getCacheDir(), "audio.wav");
                    FileOutputStream output = new FileOutputStream(localAudioFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    output.close();
                    input.close();
                    success = true;
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e("AudioDownload", "✅ Fehler beim Download", e);
            }

            boolean finalSuccess = success;
            mainHandler.post(() -> {
                if (finalSuccess) {
                    AudioPlayerHelper.playAudioFile(this, localAudioFile.getAbsolutePath(), () ->
                            Toast.makeText(this, "Wiedergabe beendet", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    Toast.makeText(AudioPlayActivity.this, "Fehler beim Download der Audio-Datei", Toast.LENGTH_LONG).show();
                }
            });

        });
    }


    @Override
    public void onMessageReceived(String jsonText) {
        runOnUiThread(() -> {
            ServerResponseHandler handler = new ServerResponseHandler();
            ResponseResult result = handler.getResponseType(jsonText);

            if (AudioPlayerHelper.isPlaying()) {
                AudioPlayerHelper.setOnCompletionListener(mp -> handleServerResponse(result));
            } else {
                handleServerResponse(result);
            }
        });
    }


    private void handleServerResponse(ResponseResult result) {
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
        Log.d("AudioPlayActivity", "📨 Systemnachricht: " + systemText);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (localAudioFile != null && localAudioFile.exists()) {
            if (localAudioFile.delete()) {
                Toast.makeText(this, "✅ Audio gelöscht!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "❌ Fehler beim Löschen der Audio!", Toast.LENGTH_SHORT).show();
            }
        }

        executor.shutdown();
    }

}
