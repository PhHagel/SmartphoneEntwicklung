package com.philipp.dv_projekt;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayActivity extends AppCompatActivity implements WebSocketCallback {

    private MediaPlayer player;
    private static final String AUDIO_URL = "http://192.168.10.128:3000/download/sprache";
    private File localAudioFile;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);

        downloadAndPlayAudio();
    }

    private void downloadAndPlayAudio() {
        executor.execute(() -> {
            boolean success = false;
            try {
                URL url = new URL(AUDIO_URL);
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
                Log.e("AudioDownload", "Fehler beim Download", e);
            }

            boolean finalSuccess = success;
            mainHandler.post(() -> {
                if (finalSuccess) {
                    // TODO hier muss evtl noch eingetragen werden, dass eine nachricht kommt, dass der roboter im wartezimmer ist
                    playDownloadedAudio();
                } else {
                    Toast.makeText(AudioPlayActivity.this, "Fehler beim Download der Audio-Datei", Toast.LENGTH_LONG).show();
                }
            });

        });
    }

    private void playDownloadedAudio() {
        if (player != null) {
            player.release();
        }

        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());

        player.setOnPreparedListener(MediaPlayer::start);
        player.setOnCompletionListener(mp -> {
            Toast.makeText(this, "Wiedergabe beendet", Toast.LENGTH_SHORT).show();
            mp.release();
        });

        try {
            player.setDataSource(localAudioFile.getAbsolutePath());
            player.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "Fehler beim Starten: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMessageReceived(String jsonText) {

        runOnUiThread(() -> {

            ServerResponseHandler handler = new ServerResponseHandler();
            ResponseResult result = handler.getResponseType(jsonText);

            if (player.isPlaying()) {

                player.setOnCompletionListener(mp -> handleServerResponse(result));

            } else {

                handleServerResponse(result);

            }

        });

    }

    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("AudioPlayActivity", "📨 Systemnachricht: " + systemText);
    }

    private void  handleServerResponse(ResponseResult result) {

        switch (result.getType()) {

            case FAILURE:
                Toast.makeText(this, "Fehler: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                break;

            case ROBOT_REACHED_GOAL:
                startActivity(new Intent(this, SmartphoneBackActivity.class));
                onDestroy();
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
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }

        if (localAudioFile != null && localAudioFile.exists()) {
            if (localAudioFile.delete()) {
                Toast.makeText(this, "✅ Audio gelöscht!", Toast.LENGTH_SHORT).show();
                } else {
                Toast.makeText(this, "❌ Fehler beim Löschen der Audio!", Toast.LENGTH_SHORT).show();
            }
        }

        executor.shutdown(); // Executor beenden
    }
}
