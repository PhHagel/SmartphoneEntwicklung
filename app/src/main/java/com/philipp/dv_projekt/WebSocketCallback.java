package com.philipp.dv_projekt;

public interface WebSocketCallback {
    void onMessageReceived(String jsonText);
    void onSystemMessageReceived(String systemText);
}
