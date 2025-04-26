package com.philipp.dv_projekt;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerResponseHandler {

    public ResponseType getResponseType(String jsonString) {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            if (json.has("type")) {
                String type = json.get("type").getAsString();

                switch (type) {
                    case "Robot_reached_Goal":
                        Log.d("ServerResponseHandler", "✅ 9999999999999999");
                        Log.d("ServerResponseHandler", "✅ 9999999999999999");
                        Log.d("ServerResponseHandler", "✅ 9999999999999999");
                        return ResponseType.ROBOT_REACHED_GOAL;

                    case "Known_Customer":
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        return ResponseType.KNOWN_CUSTOMER;

                    case "Unknown_Customer":
                        Log.d("ServerResponseHandler", "✅ 77777777777777");
                        Log.d("ServerResponseHandler", "✅ 77777777777777");
                        Log.d("ServerResponseHandler", "✅ 77777777777777");
                        return ResponseType.UNKNOWN_CUSTOMER;

                    default:
                        Log.d("ServerResponseHandler", "✅ 6666666666666666");
                        Log.d("ServerResponseHandler", "✅ 6666666666666666");
                        Log.d("ServerResponseHandler", "✅ 6666666666666666");
                        return ResponseType.UNKNOWN;
                }

            } else if (json.has("Success") && json.get("Success").getAsString().equals("TRUE") && json.get("message").isJsonObject()) {
                Log.d("ServerResponseHandler", "✅ 111111111111111111");
                Log.d("ServerResponseHandler", "✅ 111111111111111111");
                Log.d("ServerResponseHandler", "✅ 111111111111111111");
                return ResponseType.PERSON_DATA;

            } else if (json.has("Success") && json.get("Success").getAsString().equals("FALSE")) {
                Log.d("ServerResponseHandler", "✅ 2222222222222222");
                Log.d("ServerResponseHandler", "✅ 2222222222222222");
                Log.d("ServerResponseHandler", "✅ 2222222222222222");
                return ResponseType.PERSON_DATA_SUCCESS_FALSE;

            } else if (json.has("Success") && json.get("message").isJsonPrimitive()) {
                Log.d("ServerResponseHandler", "✅ 3333333333333333");
                Log.d("ServerResponseHandler", "✅ 3333333333333333");
                Log.d("ServerResponseHandler", "✅ 3333333333333333");
                return ResponseType.TERMIN_INFO;

            } else if (json.has("Date") && json.has("Time") && json.has("Weekday")) {
                Log.d("ServerResponseHandler", "✅ 4444444444444444");
                Log.d("ServerResponseHandler", "✅ 4444444444444444");
                Log.d("ServerResponseHandler", "✅ 4444444444444444");
                return ResponseType.DATE_TIME;

            } else {
                Log.d("ServerResponseHandler", "✅ 5555555555555555");
                Log.d("ServerResponseHandler", "✅ 5555555555555555");
                Log.d("ServerResponseHandler", "✅ 5555555555555555");
                return ResponseType.UNKNOWN;
            }

        } catch (Exception e) {
            Log.d("ServerResponseHandler", "⚠️ 1010101010101010101010");
            return ResponseType.UNKNOWN;
        }
    }
}