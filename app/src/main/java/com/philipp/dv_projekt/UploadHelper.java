package com.philipp.dv_projekt;

import androidx.annotation.NonNull;
import java.io.File;
import java.io.IOException;
import okhttp3.*;

public class UploadHelper {

    public static void uploadImage(File imageFile, String serverUrl, OkHttpClient client) {

        MediaType mediaType = MediaType.parse("image/jpeg");

        RequestBody fileBody = RequestBody.create(imageFile, mediaType);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("myfile", imageFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("❌ onFailure Fehler :" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    System.out.println("✅ Upload erfolgreich: " + response.body().string());
                } else {
                    System.out.println("❌ Response Fehler: " + response.code());
                }
            }
        });
    }


    public static void uploadAudio(File audioFile, String serverUrl, OkHttpClient client) {

        MediaType mediaType = MediaType.parse("audio/m4a");

        RequestBody fileBody = RequestBody.create(audioFile, mediaType);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("myfile", audioFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("❌ Fehler :" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    System.out.println("✅ Upload erfolgreich: " + response.body().string());
                } else {
                    System.out.println("❌ Fehler: " + response.code());
                }
            }
        });
    }

}
