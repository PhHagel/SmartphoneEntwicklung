package com.philipp.dv_projekt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.airbnb.lottie.LottieAnimationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements WebSocketCallback {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private PreviewView previewView;
    private Intent timeoutIntent;
    private Button bBildAufnehmen;
    private LottieAnimationView sendToServerAnimation;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_MICROPHONE = 101;
    private static final int REQUEST_MEDIA_IMAGES = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bBildAufnehmen = findViewById(R.id.bCapture);
        previewView = findViewById(R.id.previewView);
        sendToServerAnimation = findViewById(R.id.sendToServerAnimation);
        timeoutIntent = new Intent(this, TimeoutService.class);

        checkAndRequestPermissions();

        WebSocketManager.getInstance().setCallback(this);
        WebSocketManager.getInstance().connect();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bBildAufnehmen.setOnClickListener(v -> {
            bBildAufnehmen.setEnabled(false);
            capturePhoto();
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                Log.e("MainActivity", "ExecutionException beim Abrufen des CameraProviders", e);
            } catch (InterruptedException e) {
                Log.e("MainActivity", "InterruptedException beim Abrufen des CameraProviders", e);
            }
        }, getExecutor());

        AudioPlayerHelper.playAudio(this, R.raw.bildaufnehmen, () -> startService(timeoutIntent));

    }


    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_MEDIA_IMAGES);
        }
    }


    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

    }


    private void capturePhoto() {

        File photoDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Pictures");
        if (!photoDir.exists() && !photoDir.mkdirs()) {
            Toast.makeText(this, "❌ Fehler: Konnte Verzeichnis nicht erstellen!", Toast.LENGTH_LONG).show();
            return;
        }

        String fileName = "photo_" + System.currentTimeMillis() + ".jpg";
        File photoFile = new File(photoDir, fileName);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    Log.d("MainActivity", "✅ Foto gespeichert unter: " + photoFile.getAbsolutePath());
                    showPhoto(photoFile);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Toast.makeText(MainActivity.this, "❌ Fehler beim Verarbeiten: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );
    }


    private void showPhoto(File photoFile) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_photo_preview);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageCheckView = dialog.findViewById(R.id.imageCheckView);
        Button imageDeleteBtn = dialog.findViewById(R.id.imageDeleteBtn);
        Button imageAcceptBtn = dialog.findViewById(R.id.imageAcceptBtn);

        imageCheckView.setImageURI(Uri.fromFile(photoFile));
        imageCheckView.setScaleX(-1f); // Vorschau spiegeln
        dialog.show();

        imageDeleteBtn.setOnClickListener(v -> {
            bBildAufnehmen.setEnabled(true);
            if (photoFile.delete()) {
                Toast.makeText(this, "✅ Foto gelöscht!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "❌ Fehler beim Löschen des Fotos!", Toast.LENGTH_SHORT).show();
            }
        });

        imageAcceptBtn.setOnClickListener(v -> {
            dialog.dismiss();

            previewView.setVisibility(View.GONE);
            sendToServerAnimation.setVisibility(View.VISIBLE);
            sendToServerAnimation.playAnimation();
            sendToServerAnimation.setRepeatCount(300);

            UploadHelper.uploadImage(photoFile, Konstanten.UPLOAD_BILD_URL, OkHttpManager.getInstance());

            this.stopService(timeoutIntent);

            AudioPlayerHelper.playAudio(this, R.raw.phototoserver, null);
        });
    }


    @Override
    public void onMessageReceived(String jsonText) {

        runOnUiThread(() -> {
            ServerResponseHandler handler = new ServerResponseHandler();
            ResponseResult result = handler.getResponseType(jsonText);


            if (AudioPlayerHelper.isPlaying()) {
                AudioPlayerHelper.setOnCompletionListener(mp -> handleServerResponse(result, jsonText));
            } else {
                handleServerResponse(result, jsonText);
            }

        });

    }


    private void  handleServerResponse(ResponseResult result, String jsonText) {

        switch (result.getType()) {

            case AUDIO_GENERATION_REQUEST_FAILURE:
                Toast.makeText(this, "Audiogenerierungsfehler: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                break;

            case AUDIO_GENERATION_REQUEST_SUCCESS:
                startActivity(new Intent(this, AudioPlayActivity.class));
                finish();
                break;

            case FAILURE:
                Toast.makeText(this, "Fehler: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                break;

            case KNOWN_CUSTOMER:
                JsonObject json = JsonParser.parseString(jsonText).getAsJsonObject();
                String appointment = json.has("appointment") ? json.get("appointment").getAsString() : "FALSE";
                if (appointment.equalsIgnoreCase("TRUE")) {
                    startActivity(new Intent(this, FollowRoboActivity.class));
                } else {
                    startActivity(new Intent(this, RecordTerminActivity.class));
                }
                finish();
                break;

            case KNOWN_CUSTOMER_WITHOUT_APPOINTMENT:
                startActivity(new Intent(this, RecordTerminActivity.class));
                finish();
                break;

            case UNKNOWN_CUSTOMER:
                startActivity(new Intent(this, RecordPersonActivity.class));
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
        Log.d("MainActivity", "📨 Systemnachricht: " + systemText);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Intent reset = new Intent("com.philipp.ACTION_RESET_TIMEOUT");
        LocalBroadcastManager.getInstance(this).sendBroadcast(reset);

        return super.dispatchTouchEvent(ev);
    }

}