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

public class RecordActivity extends AppCompatActivity implements WebSocketCallback {


    private MediaRecorder recorder;
    private String filePath;
    private Button startBtn;
    private Button stopBtn;
    private MediaPlayer player;
    private File audioFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

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
        player = MediaPlayer.create(RecordActivity.this, R.raw.tonaufnehmen);
        player.start();

        player.setOnCompletionListener(mp -> {
            Intent timeoutIntent = new Intent(this, TimeoutService.class);
            startService(timeoutIntent);
        });

        startBtn.setOnClickListener(v -> {

            stopService(new Intent(this, TimeoutService.class));

            String audioFileName = "person-" + System.currentTimeMillis();
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

            // Hier wird die Audio zum Server gesendet
            audioFile = new File(filePath);
            UploadHelper.uploadAudio(audioFile, "http://192.168.10.128:3000/upload/sprache", OkHttpManager.getInstance());

            // Audio abspielen (noch nicht da)
            // Hier wird Audio abgespielt (noch nicht da)
            if (player != null) {
                player.release();
                player = null;
            }
            player = MediaPlayer.create(RecordActivity.this, R.raw.audiotoserver);
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

            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }


    @Override
    public void onMessageReceived(String jsonText) {

        runOnUiThread(() -> {
            ServerResponseHandler handler = new ServerResponseHandler();
            ResponseResult result = handler.getResponseType(jsonText);

            if (player.isPlaying()) {

                player.setOnCompletionListener(mp -> handleServerResponse(result, jsonText));

            } else {

                handleServerResponse(result, jsonText);

            }

        });

    }


    private void  handleServerResponse(ResponseResult result, String jsonText) {

        stopService(new Intent(this, TimeoutService.class));

        switch (result.getType()) {

            // TODO wenn Hendrik sagt, dass das weg soll, muss das ersetzt werden mit dem anderen case xD
            case PERSON_DATA:
                PersonResponse person = new Gson().fromJson(jsonText, PersonResponse.class);
                String nachname = person.message.lastname;
                String vorname = person.message.firstname;
                String geschlecht = person.message.sex;
                String geburtsdatum = person.message.dateOfBirth;
                String telefon = person.message.phoneNumber;
                String email = person.message.emailAddress; //schauen wegen @
                showNewUserData("Nachname: " + nachname + "\nVorname: " + vorname + "\nGeschlecht: " + geschlecht +
                        "\nGeburtsdatum: " + geburtsdatum + "\nTelefonnummer: " + telefon + "\nE-Mail: " + email);
                break;

            case PERSON_DATA_SUCCESS_FALSE:
                Toast .makeText(this, "❌ Personendaten nicht erfolgreich neu versuchen", Toast.LENGTH_SHORT).show();
                break;

            default:
                Log.d("RecordActivity", "❓ Unbekannter Type vom Server: " + result.getType());
                Log.d("RecordActivity", "❓ Unbekannte Antwort vom Server: " + jsonText);
                Toast.makeText(this, "❓ Unbekannte Antwort vom Server", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onSystemMessageReceived(String systemText) {
        Log.d("RecordActivity", "📨 Systemnachricht: " + systemText);
    }


    private void showNewUserData(String userDataFromServer) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_new_user);

        TextView textCheckView = dialog.findViewById(R.id.textCheckView);
        Button userDeleteBtn = dialog.findViewById(R.id.userDeleteBtn);
        Button userAcceptBtn = dialog.findViewById(R.id.userAcceptBtn);

        userDeleteBtn.setOnClickListener(v -> {
            if (audioFile.delete()) {
                WebSocketManager.getInstance().sendMessage("user_declined");
                Toast.makeText(this, "✅ Person gelöscht. Bitte neu versuchen", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "❌ Fehler beim Löschen der Person!", Toast.LENGTH_SHORT).show();
            }
        });

        userAcceptBtn.setOnClickListener(v -> {
            WebSocketManager.getInstance().sendMessage("user_accepted");
            Toast.makeText(this, "✅ Person akzeptiert!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, RecordTerminActivity.class));
            dialog.dismiss();
        });

        textCheckView.setText(userDataFromServer);

        dialog.show();
    }
}
