package com.philipp.dv_projekt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerResponseHandler {

    public ResponseResult getResponseType(String jsonString) {
        try {

            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

            if (json.has("type")) {
                String type = json.get("type").getAsString();

                switch (type) {

                    // Für Ausruf von Patienten
                    case "AUDIO_GENERATION_REQUEST_FAILURE":
                        if (json.has("message")) {
                            String message = json.get("message").getAsString();
                            return new ResponseResult(ResponseType.AUDIO_GENERATION_REQUEST_FAILURE, message);
                        }
                        return new ResponseResult(ResponseType.FAILURE, "Keine Message in AUDIO_GENERATION_REQUEST_FAILURE vorhanden");

                    // Für Ausruf von Patienten
                    case "AUDIO_GENERATION_REQUEST_SUCCESS":
                        return new ResponseResult(ResponseType.AUDIO_GENERATION_REQUEST_SUCCESS, null);

                    //
                    case "EXTRACT_DATA_FROM_AUDIO_SUCCESS":
                        if (json.has("message")) {
                            String message = json.get("message").getAsString();
                            return new ResponseResult(ResponseType.EXTRACT_DATA_FROM_AUDIO_SUCCESS, message);
                        }
                        return new ResponseResult(ResponseType.FAILURE, "Keine Message vorhanden");

                    // Fehler vom Server erhalten
                    case "FAILURE":
                        if (json.has("message")) {
                            String message = json.get("message").getAsString();
                            return new ResponseResult(ResponseType.FAILURE, message);
                        }
                        return new ResponseResult(ResponseType.FAILURE, "Keine Message vorhanden");


                    // Für erkannte Patienten
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

                    // Nächster Termin übergeben
                    case "NEXT_APPOINTMENT":
                        return new ResponseResult(ResponseType.NEXT_APPOINTMENT, jsonString);

                    //
                    case "PERSON_DATA":
                        if (json.has("success")) {
                            String successStr = json.get("success").getAsString();
                            if ("success".equalsIgnoreCase(successStr)) {
                                return new ResponseResult(ResponseType.PERSON_DATA, null);
                            } else {
                                return new ResponseResult(ResponseType.PERSON_DATA_SUCCESS_FALSE, null);
                            }
                        }
                        return new ResponseResult(ResponseType.FAILURE, "success fehlt in PERSON_DATA");

                    // Wenn Roboter das Ziel erreicht hat
                    case "ROBOTER_REACHED_GOAL":
                        return new ResponseResult(ResponseType.ROBOT_REACHED_GOAL, null);

                    // Timeout vom Server erhalten
                    case "TIMEOUT":
                        return new ResponseResult(ResponseType.FAILURE, "Timeout vom Server erhalten");

                    // Wenn patient nicht erkannt wurde
                    case "UNKNOWN_CUSTOMER":
                        return new ResponseResult(ResponseType.UNKNOWN_CUSTOMER, null);

                    // Default Type
                    default:
                        return new ResponseResult(ResponseType.UNKNOWN_RESPONSE, "Type nicht vorhanden, oder noch nicht implementiert");


                }
            } else {
                return new ResponseResult(ResponseType.UNKNOWN_RESPONSE, "Kein Type vorhanden");
            }

        } catch (Exception e) {
            System.err.println("Exception beim JSON-Parsing: " + e.getMessage());
            return new ResponseResult(ResponseType.UNKNOWN_RESPONSE, "Exception beim JSON-Parsing: " + e.getMessage());
        }
    }
}