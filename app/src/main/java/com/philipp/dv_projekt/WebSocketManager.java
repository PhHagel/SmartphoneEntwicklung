package com.philipp.dv_projekt;

import android.util.Log;

public class WebSocketManager {

    private static WebSocketManager instance;
    private final WebSocketClient webSocketClient;
    private static final String TAG = "WebSocketManager";


    private WebSocketManager() {
        webSocketClient = new WebSocketClient();
    }


    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }


    public void connect() {
        Log.d(TAG, "🔌 WebSocket connect() aufgerufen");
        webSocketClient.connect();

    }


    public void setCallback(WebSocketCallback callback) {
        Log.d(TAG, "📞 Callback wurde gesetzt");
        webSocketClient.setCallback(callback);
    }


    public void sendMessage(String message) {
        Log.d(TAG, "📤 Sende Nachricht: " + message);
        webSocketClient.sendMessage(message);
    }

}
