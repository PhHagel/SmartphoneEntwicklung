package com.philipp.dv_projekt;

import androidx.annotation.NonNull;
import com.google.gson.JsonSyntaxException;
import okhttp3.*;

public class WebSocketClient {

    private WebSocket webSocket;
    private WebSocketCallback callback;


    public void setCallback(WebSocketCallback callback) {
        this.callback = callback;
    }

    public void connect() {
        // Holt den Client direkt aus dem Singleton
        OkHttpClient client = OkHttpManager.getInstance();

        Request request = new Request.Builder()
                .url(Konstanten.WS_URL)  // ← IP des Servers samt Port
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

                // Systemnachrichten filtern
                if (!text.trim().startsWith("{")) {
                    if (callback != null) {
                        callback.onSystemMessageReceived(text);
                    }
                    return;
                }

                if (callback != null) {
                    callback.onMessageReceived(text);
                }

                try {
                    ServerResponseHandler handler = new ServerResponseHandler();
                    handler.getResponseType(text);
                } catch (JsonSyntaxException e) {
                    System.out.println("⚠️ Ungültiges JSON: " + text);
                } catch (IllegalStateException e) {
                    System.out.println("⚠️ JSON ist kein Objekt: " + text);
                }
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
