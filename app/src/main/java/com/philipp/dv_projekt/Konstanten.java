package com.philipp.dv_projekt;

public class Konstanten {
    public static final String SERVER_IP = "192.168.10.128";
    public static final String API_PORT = "3000";
    public static final String WS_PORT = "3001";
    public static final String UPLOAD_SPRACHE_ORDNER = "upload/sprache";
    public static final String UPLOAD_BILD_ORDNER = "upload/gesicht";
    public static final String DOWNLOAD_SPRACHE_ORDNER = "download/sprache";
    public static final String UPLOAD_BILD_URL = "http://" + SERVER_IP + ":" + API_PORT + "/" + UPLOAD_BILD_ORDNER;
    public static final String UPLOAD_SPRACHE_URL = "http://" + SERVER_IP + ":" + API_PORT + "/" + UPLOAD_SPRACHE_ORDNER;
    public static final String DOWNLOAD_SPRACHE_URL = "http://" + SERVER_IP + ":" + API_PORT + "/" + DOWNLOAD_SPRACHE_ORDNER;
    public static final String WS_URL = "ws://" + SERVER_IP + ":" + WS_PORT;
    public static final int LOTTY_REPEAT_COUNT = 3000;

}
