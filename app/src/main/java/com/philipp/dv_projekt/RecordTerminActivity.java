package com.philipp.dv_projekt;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class RecordTerminActivity extends AppCompatActivity {
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String filePath;
    private Button startBtn;
    private Button stopBtn;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_termin);

        startBtn = findViewById(R.id.btn_start_recording);
        stopBtn = findViewById(R.id.btn_stop_recording);
        Button closeBtn = findViewById(R.id.btn_closePage);

        stopBtn.setEnabled(false);

        LottieAnimationView aufnahmeAnimation = findViewById(R.id.aufnahmeAnimation);

        if (player == null) {
            player = MediaPlayer.create(RecordTerminActivity.this, R.raw.terminannehmen);
        }

        startBtn.setOnClickListener(v -> {
            String audioFileName = "audio_" + System.currentTimeMillis();
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
            File audioFile = new File(filePath);
            UploadHelper.uploadAudio(audioFile, "http://192.168.10.128:5000/upload", client);

            // Jackson für json mapper
            // Hier soll die Serverantwort verarbeitet werden

            String datum = "15.07.2025";
            String tag = "Dienstag";
            String von = "15:00 Uhr";
            String bis = "15:30 Uhr";

            showNewUserData("Datum: " + datum + "\nTag: " + tag + "\nVon: " + von +
                    "\nBis: " + bis);
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

    private void showNewUserData(String userDataFromServer) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_new_user);

        TextView textCheckView = dialog.findViewById(R.id.textCheckView);
        Button userDeleteBtn = dialog.findViewById(R.id.userDeleteBtn);
        Button userAcceptBtn = dialog.findViewById(R.id.userAcceptBtn);

        userDeleteBtn.setOnClickListener(v -> {
            File audioFile = new File(filePath);
            if (audioFile.delete()) {
                Toast.makeText(this, "✅ Audio gelöscht!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "❌ Fehler beim Löschen der Audio!", Toast.LENGTH_SHORT).show();
            }
        });

        userAcceptBtn.setOnClickListener(v -> {
            Toast.makeText(this, "✅ Foto akzeptiert!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            // Hier muss das OK an den Server gesendet werden


        });

        textCheckView.setText(userDataFromServer);

        dialog.show();
    }
}
