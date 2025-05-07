package com.philipp.dv_projekt;

import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerResponseHandler {

    public ResponseResult getResponseType(String jsonString) {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String message = json.get("message").getAsString();

            if (json.has("type")) {
                String type = json.get("type").getAsString();

                switch (type) {
                    case "Robot_reached_Goal":
                        Log.d("ServerResponseHandler", "✅ 9999999999999999");
                        Log.d("ServerResponseHandler", "✅ 9999999999999999");
                        Log.d("ServerResponseHandler", "✅ 9999999999999999");
                        return new ResponseResult(ResponseType.ROBOT_REACHED_GOAL, null);

                    case "Audio_Generation_Request_Success":
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        return new ResponseResult(ResponseType.AUDIO_GENERATION_REQUEST_SUCCESS, null);

                    case "Known_Customer":
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        Log.d("ServerResponseHandler", "✅ 88888888888888");
                        return new ResponseResult(ResponseType.KNOWN_CUSTOMER, null);

                    case "Unknown_Customer":
                        Log.d("ServerResponseHandler", "✅ 77777777777777");
                        Log.d("ServerResponseHandler", "✅ 77777777777777");
                        Log.d("ServerResponseHandler", "✅ 77777777777777");
                        return new ResponseResult(ResponseType.UNKNOWN_CUSTOMER, null);

                    case "FAILURE":
                        message = json.get("message").getAsString();
                        return new ResponseResult(ResponseType.FAILURE, message);

                    default:
                        Log.d("ServerResponseHandler", "✅ 6666666666666666");
                        Log.d("ServerResponseHandler", "✅ 6666666666666666");
                        Log.d("ServerResponseHandler", "✅ 6666666666666666");
                        return new ResponseResult(ResponseType.UNKNOWN, message);
                }

            } else if (json.has("Success") && json.get("Success").getAsString().equals("TRUE") && json.get("message").isJsonObject()) {
                Log.d("ServerResponseHandler", "✅ 111111111111111111");
                Log.d("ServerResponseHandler", "✅ 111111111111111111");
                Log.d("ServerResponseHandler", "✅ 111111111111111111");
                return new ResponseResult(ResponseType.PERSON_DATA, null);

            } else if (json.has("Success") && json.get("Success").getAsString().equals("FALSE")) {
                Log.d("ServerResponseHandler", "✅ 2222222222222222");
                Log.d("ServerResponseHandler", "✅ 2222222222222222");
                Log.d("ServerResponseHandler", "✅ 2222222222222222");
                return new ResponseResult(ResponseType.PERSON_DATA_SUCCESS_FALSE, null);

            } else if (json.has("Success") && json.get("message").isJsonPrimitive()) {
                Log.d("ServerResponseHandler", "✅ 3333333333333333");
                Log.d("ServerResponseHandler", "✅ 3333333333333333");
                Log.d("ServerResponseHandler", "✅ 3333333333333333");
                return new ResponseResult(ResponseType.TERMIN_INFO, null);

            } else if (json.has("Date") && json.has("Time") && json.has("Weekday")) {
                Log.d("ServerResponseHandler", "✅ 4444444444444444");
                Log.d("ServerResponseHandler", "✅ 4444444444444444");
                Log.d("ServerResponseHandler", "✅ 4444444444444444");
                return new ResponseResult(ResponseType.DATE_TIME, null);

            } else {
                Log.d("ServerResponseHandler", "✅ 5555555555555555");
                Log.d("ServerResponseHandler", "✅ 5555555555555555");
                Log.d("ServerResponseHandler", "✅ 5555555555555555");
                return new ResponseResult(ResponseType.UNKNOWN, null);
            }

        } catch (Exception e) {
            Log.d("ServerResponseHandler", "⚠️ 1010101010101010101010");
            return new ResponseResult(ResponseType.UNKNOWN, null);
        }
    }
}