package com.philipp.dv_projekt;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import okhttp3.OkHttpClient;

public class RecordTerminActivity extends AppCompatActivity implements WebSocketCallback {
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String filePath;
    private Button startBtn;
    private Button stopBtn;
    private File audioFile;
    private final OkHttpClient client = new OkHttpClient();
    private final WebSocketClient webSocketClient = new WebSocketClient();

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

        webSocketClient.setCallback(this);
        webSocketClient.connect(client);

        LottieAnimationView aufnahmeAnimation = findViewById(R.id.aufnahmeAnimation);

        if (player != null) {
            player.release();
            player = null;
        }
        player = MediaPlayer.create(RecordTerminActivity.this, R.raw.terminannehmen);
        player.start();

        startBtn.setOnClickListener(v -> {
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
            UploadHelper.uploadAudio(audioFile, "http://192.168.10.128:3000/upload/sprache", client);

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

            Intent intent = new Intent(RecordTerminActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onMessageReceived(String jsonText) {
        player.setOnCompletionListener(mp -> {
            // Dieser Block wird aufgerufen, sobald die Audiodatei zu Ende gespielt ist:
            runOnUiThread(() -> {
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
                        if (antwort.equals("Nein")) {
                            Toast.makeText(this, "❌ Termin abgelehnt!", Toast.LENGTH_SHORT).show();
                            if (player != null) {
                                player.release();
                                player = null;
                            }
                            player = MediaPlayer.create(RecordTerminActivity.this, R.raw.abgelehntertermin);
                            player.start();
                        } else if (antwort.equals("Ja")) {
                            Toast.makeText(this, "✅ Termin akzeptiert!", Toast.LENGTH_SHORT).show();
                            if (player != null) {
                                player.release();
                                player = null;
                            }
                            player = MediaPlayer.create(RecordTerminActivity.this, R.raw.angenommenertermin);
                            player.start();
                        } else {
                            Toast.makeText(this, "❓ Unbekannte Antwort vom Server", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        Toast.makeText(this, "❓ Unbekannte Antwort vom Server", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });
    }

    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("RecordTerminActivity", "📨 Systemnachricht: " + systemText);
    }
}
