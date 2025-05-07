package com.philipp.dv_projekt;

public class ResponseResult {
    private final ResponseType type;
    private final String message;

    public ResponseResult(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ResponseType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}