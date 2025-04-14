package com.philipp.dv_projekt;

import androidx.annotation.NonNull;
import okhttp3.*;

public class WebSocketClient {

    private WebSocket webSocket;

    public void connect(OkHttpClient client) {


        Request request = new Request.Builder()
                .url("ws://192.168.10.128:3001") // ← 10.0.2.2 statt localhost im Emulator!
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("✅ WebSocket geöffnet!");
                sendMessage("sm");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {

                System.out.println("📨 Nachricht empfangen: " + text);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                System.out.println("❌ Fehler: " + t.getMessage());
            }
        });

    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "App geschlossen");
        }
    }
}
