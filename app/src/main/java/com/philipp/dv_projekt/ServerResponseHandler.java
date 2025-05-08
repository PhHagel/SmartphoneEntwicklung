package com.philipp.dv_projekt;

import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerResponseHandler {

    public ResponseResult getResponseType(String jsonString) {
        try {

            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            if (json.has("type")) {
                String type = json.get("type").getAsString();

                switch (type) {


                    case "AUDIO_GENERATION_REQUEST_FAILURE":
                        if (json.has("message")) {
                            String message = json.get("message").getAsString();
                            return new ResponseResult(ResponseType.AUDIO_GENERATION_REQUEST_FAILURE, message);
                        }
                        return new ResponseResult(ResponseType.FAILURE, "Keine Message in AUDIO_GENERATION_REQUEST_FAILURE vorhanden");


                    case "AUDIO_GENERATION_REQUEST_SUCCESS":
                        return new ResponseResult(ResponseType.AUDIO_GENERATION_REQUEST_SUCCESS, null);

                    case "EXTRACT_DATA_FROM_AUDIO_SUCCESS":
                        return new ResponseResult(ResponseType.EXTRACT_DATA_FROM_AUDIO_SUCCESS, null);

                    case "FAILURE":  //genereller fehler evtl für timeouts
                        if (json.has("message")) {
                            String message = json.get("message").getAsString();
                            return new ResponseResult(ResponseType.FAILURE, message);
                        }
                        return new ResponseResult(ResponseType.FAILURE, "Keine Message vorhanden");


                    case "KNOWN_CUSTOMER":
                        if (json.has("appointment")) {
                            boolean appointment = json.get("appointment").getAsBoolean();
                            if (appointment) {
                                return new ResponseResult(ResponseType.KNOWN_CUSTOMER, null);
                            } else {
                                return new ResponseResult(ResponseType.KNOWN_CUSTOMER_WITHOUT_APPOINTMENT, null);
                            }
                        }
                        return new ResponseResult(ResponseType.FAILURE, "appointment fehlt in KNOWN_CUSTOMER");


                    case "NEXT_APPOINTMENT":
                        return new ResponseResult(ResponseType.NEXT_APPOINTMENT, null);


                    case "PERSON_DATA":
                        if (json.has("success")) {
                            boolean success = json.get("success").getAsBoolean();
                            if (success) {
                                return new ResponseResult(ResponseType.PERSON_DATA, null);
                            } else {
                                return new ResponseResult(ResponseType.PERSON_DATA_SUCCESS_FALSE, null);
                            }
                        }
                        return new ResponseResult(ResponseType.FAILURE, "success fehlt in PERSON_DATA");


                    case "ROBOTER_REACHED_GOAL":
                        return new ResponseResult(ResponseType.ROBOT_REACHED_GOAL, null);


                    case "TERMIN_INFO":
                        return new ResponseResult(ResponseType.TERMIN_INFO, null);

                    case "TIMEOUT":
                        return new ResponseResult(ResponseType.FAILURE, "Timeout vom Server erhalten");


                    case "UNKNOWN_CUSTOMER":
                        return new ResponseResult(ResponseType.UNKNOWN_CUSTOMER, null);


                    default:
                        return new ResponseResult(ResponseType.UNKNOWN_RESPONSE, "Type nicht vorhanden, oder noch nicht implementiert");


                }
            } else {
                return new ResponseResult(ResponseType.UNKNOWN_RESPONSE, "Kein Type vorhanden");
            }

        } catch (Exception e) {
            Log.e("ServerResponseHandler", "Exception beim Parsen des JSON!", e);
            return new ResponseResult(ResponseType.UNKNOWN_RESPONSE, "Exception beim JSON-Parsing: " + e.getMessage());
        }
    }
}