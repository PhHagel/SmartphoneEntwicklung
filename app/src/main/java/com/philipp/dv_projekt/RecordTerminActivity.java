package com.philipp.dv_projekt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class RecordTerminActivity extends AppCompatActivity implements WebSocketCallback {
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String filePath;
    private Button startBtn;
    private Button stopBtn;
    private File audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_termin);

        TextView datumTextView = findViewById(R.id.DatenTextTermin);
        String text = String.format("Datum: %s%nTag: %s%nZeit: %s",
                "15.06.2000", "Fr", "15:00");
        datumTextView.setText(text);

        startBtn = findViewById(R.id.btn_start_recording);
        stopBtn = findViewById(R.id.btn_stop_recording);
        Button closeBtn = findViewById(R.id.btn_closePage);

        stopBtn.setEnabled(false);

        WebSocketManager.getInstance().setCallback(this);

        LottieAnimationView aufnahmeAnimation = findViewById(R.id.aufnahmeAnimation);

        if (player != null) {
            player.release();
            player = null;
        }
        player = MediaPlayer.create(RecordTerminActivity.this, R.raw.terminannehmen);
        player.start();

        player.setOnCompletionListener(mp -> {
            Intent timeoutIntent = new Intent(this, TimeoutService.class);
            startService(timeoutIntent);
        });

        startBtn.setOnClickListener(v -> {

            stopService(new Intent(this, TimeoutService.class));

            String audioFileName = "command-" + System.currentTimeMillis();
            filePath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/" + audioFileName + ".m4a";

            recorder = new MediaRecorder(this);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(128000);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(filePath);

            try {
                recorder.prepare();
                recorder.start();
                startBtn.setEnabled(false);
                stopBtn.setEnabled(true);

                aufnahmeAnimation.setVisibility(View.VISIBLE);
                aufnahmeAnimation.playAnimation();
                aufnahmeAnimation.setRepeatCount(3000);
            } catch (IOException e) {
                Toast.makeText(this, "❌ Fehler beim Starten der Aufnahme", Toast.LENGTH_SHORT).show();
            }
        });

        stopBtn.setOnClickListener(v -> {
            try {
                recorder.stop();
                aufnahmeAnimation.cancelAnimation();
                aufnahmeAnimation.setProgress(0f);
                aufnahmeAnimation.setVisibility(View.INVISIBLE);
            } catch (RuntimeException e) {
                Toast.makeText(this, "❌ Fehler beim Stoppen der Aufnahme", Toast.LENGTH_SHORT).show();
            }
            recorder.release();
            recorder = null;
            stopBtn.setEnabled(false);
            startBtn.setEnabled(false);
            Toast.makeText(this, "✅ Aufnahme gespeichert unter: " + filePath, Toast.LENGTH_SHORT).show(); // nur zum Testen

            // Hier soll die Audio zum Server gesendet werden
            audioFile = new File(filePath);
            UploadHelper.uploadAudio(audioFile, "http://192.168.10.128:3000/upload/sprache", OkHttpManager.getInstance());

            if (player != null) {
                player.release();
                player = null;
            }
            player = MediaPlayer.create(RecordTerminActivity.this, R.raw.audiotoserver);
            player.start();

        });

        closeBtn.setOnClickListener(v -> {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (RuntimeException e) {
                    Toast.makeText(this, "❌ Fehler beim Stoppen der Aufnahme", Toast.LENGTH_SHORT).show();
                } finally {
                    recorder.release();
                    recorder = null;
                }
            }

            stopService(new Intent(this, TimeoutService.class));

            Intent intent = new Intent(RecordTerminActivity.this, MainActivity.class);
            startActivity(intent);
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
        stopService(new Intent(this, TimeoutService.class));

        ServerResponseHandler handler = new ServerResponseHandler();
        ResponseType type = handler.getResponseType(jsonText);

        switch (type) {
            case DATE_TIME:
                DateTimeResponse dateTimeResponse = new Gson().fromJson(jsonText, DateTimeResponse.class);

                String datum = dateTimeResponse.Date;
                String tag = dateTimeResponse.Weekday;
                String zeit = dateTimeResponse.Time;

                TextView datumTextView = findViewById(R.id.DatenTextTermin);
                String text = String.format("Datum: %s%nTag: %s%nZeit: %s", datum, tag, zeit);
                datumTextView.setText(text);
                break;

            case TERMIN_INFO:
                TerminResponse terminResponse = new Gson().fromJson(jsonText, TerminResponse.class);
                String antwort = terminResponse.message;
                if ("Nein".equals(antwort)) {
                    Toast.makeText(this, "❌ Termin abgelehnt!", Toast.LENGTH_SHORT).show();
                    resetAndPlay(R.raw.abgelehntertermin);
                } else if ("Ja".equals(antwort)) {
                    Toast.makeText(this, "✅ Termin akzeptiert!", Toast.LENGTH_SHORT).show();
                    resetAndPlay(R.raw.angenommenertermin);
                } else {
                    Toast.makeText(this, "❓ Unbekannte Antwort vom Server", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                Toast.makeText(this, "❓ Unbekannte Antwort vom Server", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void resetAndPlay(int rawResourceId) {
        if (player != null) {
            player.release();
            player = null;
        }
        player = MediaPlayer.create(this, rawResourceId);
        player.start();
    }

    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("RecordTerminActivity", "📨 Systemnachricht: " + systemText);
    }

    // hier wird bei jedem Touch-Event der Timeout in TimeoutService.java zurückgesetzt
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Reset an den Service schicken
        Intent reset = new Intent("com.philipp.ACTION_RESET_TIMEOUT");
        LocalBroadcastManager.getInstance(this).sendBroadcast(reset);

        return super.dispatchTouchEvent(ev);
    }


}
