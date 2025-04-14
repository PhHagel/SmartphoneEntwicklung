package com.philipp.dv_projekt;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerResponseHandler {

    private final Gson gson = new Gson();

    public void handleResponse(String jsonString) {
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

        if (json.has("type")) {
            String type = json.get("type").getAsString();

            switch (type) {
                case "Robot_reached_Goal":
                    RobotReachedGoalResponse robot = gson.fromJson(json, RobotReachedGoalResponse.class);
                    System.out.println("➡️ Roboter hat Ziel erreicht");
                    break;

                case "Known_Customer":
                    KnownCustomerResponse known = gson.fromJson(json, KnownCustomerResponse.class);
                    System.out.println("✅ Bekannter Patient, Termin: " + known.Appointment);
                    break;

                case "Unknown_Customer":
                    UnknownCustomerResponse unknown = gson.fromJson(json, UnknownCustomerResponse.class);
                    System.out.println("❌ Unbekannter Patient");
                    break;

                default:
                    System.out.println("❓ Unbekannter Typ: " + type);
            }

        } else if (json.has("Success") && json.get("message").isJsonObject()) {
            PersonResponse person = gson.fromJson(json, PersonResponse.class);
            System.out.println("👤 Person erkannt: " + person.message.firstname + " " + person.message.lastname);

        } else if (json.has("Success") && json.get("message").isJsonPrimitive()) {
            TerminResponse termin = gson.fromJson(json, TerminResponse.class);
            System.out.println("📅 Terminantwort: " + termin.message);

        } else if (json.has("Date") && json.has("Time") && json.has("Weekday")) {
            DateTimeResponse datetime = gson.fromJson(json, DateTimeResponse.class);
            System.out.println("🗓️ Datum: " + datetime.Date + ", Uhrzeit: " + datetime.Time + ", Tag: " + datetime.Weekday);

        } else {
            System.out.println("❗ Nicht erkannte Struktur: " + jsonString);
        }
    }
}
