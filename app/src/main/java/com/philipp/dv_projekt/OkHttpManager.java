package com.philipp.dv_projekt;

import okhttp3.OkHttpClient;

public class OkHttpManager {

    private static OkHttpClient instance;

    private OkHttpManager() {
        // private Konstruktor, damit niemand instanziert
    }

    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient();
        }
        return instance;
    }
}