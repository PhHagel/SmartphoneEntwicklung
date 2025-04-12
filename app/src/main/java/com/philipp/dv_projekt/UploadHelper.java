package com.philipp.dv_projekt;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import okhttp3.*;

public class UploadHelper {

    public static void uploadImage(File imageFile, String serverUrl, OkHttpClient client) {

        // MediaType für ein Bild (z.B. JPEG oder PNG)
        MediaType mediaType = MediaType.parse("image/jpeg");

        // RequestBody für die Bilddatei
        RequestBody fileBody = RequestBody.create(imageFile, mediaType);

        // MultipartBody (Formulardaten mit Datei)
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(), fileBody)
                // Optional: weitere Felder im Formular
                .build();

        // URL deines Servers
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();

        // Asynchroner Aufruf
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace(); // Fehlerbehandlung
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("✅ Upload erfolgreich: " + response.body().string());
                } else {
                    System.out.println("❌ Fehler: " + response.code());
                }
            }
        });
    }
}
