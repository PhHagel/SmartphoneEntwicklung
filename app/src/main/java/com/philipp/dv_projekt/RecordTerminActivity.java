package com.philipp.dv_projekt;

import android.content.Intent;
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
    private String filePath;
    private Button startBtn;
    private Button stopBtn;
    private LottieAnimationView aufnahmeGreen;
    private LottieAnimationView aufnahmeAnimation;
    private LottieAnimationView sendToServerAnimation;
    private File audioFile;
    private Intent timeoutIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_termin);

        timeoutIntent = new Intent(this, TimeoutService.class);
        startBtn = findViewById(R.id.btn_start_recording);
        stopBtn = findViewById(R.id.btn_stop_recording);
        Button closeBtn = findViewById(R.id.btn_closePage);

        stopBtn.setEnabled(false);

        aufnahmeGreen = findViewById(R.id.aufnahmeGreen);
        aufnahmeAnimation = findViewById(R.id.aufnahmeAnimation);
        sendToServerAnimation = findViewById(R.id.sendToServerAnimation);

        WebSocketManager.getInstance().setCallback(this);

        aufnahmeGreen.setVisibility(View.VISIBLE);

        AudioPlayerHelper.playAudio(this, R.raw.terminannehmen, null);


        startBtn.setOnClickListener(v -> {

            this.stopService(timeoutIntent);

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

                aufnahmeGreen.setVisibility(View.GONE);
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
                aufnahmeAnimation.setVisibility(View.GONE);
                sendToServerAnimation.setVisibility(View.VISIBLE);
                sendToServerAnimation.playAnimation();
                sendToServerAnimation.setRepeatCount(3000);
            } catch (RuntimeException e) {
                Toast.makeText(this, "❌ Fehler beim Stoppen der Aufnahme", Toast.LENGTH_SHORT).show();
            }
            recorder.release();
            recorder = null;
            stopBtn.setEnabled(false);
            startBtn.setEnabled(false);

            audioFile = new File(filePath);
            UploadHelper.uploadAudio(audioFile, Konstanten.UPLOAD_SPRACHE_URL, OkHttpManager.getInstance());

            AudioPlayerHelper.playAudio(this, R.raw.audiotoserver, null);

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

        TextView datumTextView = findViewById(R.id.DatenTextTermin);

        switch (result.getType()) {

            case NEXT_APPOINTMENT:
                NextAppointmentResponse nextAppointmentResponse = new Gson().fromJson(result.getMessage(), NextAppointmentResponse.class);

                DateTimeResponse dateTimeResponse = nextAppointmentResponse.message;

                String datum = dateTimeResponse.date;
                String tag = dateTimeResponse.weekday;
                String zeit = dateTimeResponse.time;


                String text = String.format("Datum: "+ datum + "\nTag: " + tag + "\nZeit: " + zeit);
                Log.d("RecordTerminActivity", "📅 Nächster Termin: " + text);
                datumTextView.setText(text);
                this.startService(timeoutIntent);
                break;

            case EXTRACT_DATA_FROM_AUDIO_SUCCESS:
                String antwort = result.getMessage();
                Log.d("RecordTerminActivity", "📨 Antwort JA/NEIN: " + antwort);
                if ("NO".equals(antwort)) {

                    sendToServerAnimation.setVisibility(View.GONE);
                    aufnahmeGreen.setVisibility(View.VISIBLE);

                    startBtn.setEnabled(true);
                    Toast.makeText(this, "❌ Termin abgelehnt!", Toast.LENGTH_SHORT).show();
                    resetAndPlay(R.raw.abgelehntertermin);
                    datumTextView.setText("");
                } else if ("YES".equals(antwort)) {
                                        sendToServerAnimation.setVisibility(View.GONE);
                    Toast.makeText(this, "✅ Termin akzeptiert!", Toast.LENGTH_SHORT).show();
                    resetAndPlay(R.raw.angenommenertermin);
                    if (AudioPlayerHelper.isPlaying()) {
                        AudioPlayerHelper.setOnCompletionListener(mp -> {
                            Intent intent = new Intent(RecordTerminActivity.this, StandbyActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        Intent intent = new Intent(RecordTerminActivity.this, StandbyActivity.class);
                        startActivity(intent);
                        finish();
                    }
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
        AudioPlayerHelper.playAudio(this, rawResourceId, null);
    }


    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("RecordTerminActivity", "📨 Systemnachricht: " + systemText);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Intent reset = new Intent("com.philipp.ACTION_RESET_TIMEOUT");
        LocalBroadcastManager.getInstance(this).sendBroadcast(reset);

        return super.dispatchTouchEvent(ev);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                Log.w("RecordTerminActivity", "Recorder konnte nicht gestoppt werden: " + e.getMessage());
            } finally {
                recorder.release();
                recorder = null;
            }
        }
    }

}
