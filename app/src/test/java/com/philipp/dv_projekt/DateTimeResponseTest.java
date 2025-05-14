package com.philipp.dv_projekt;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

public class DateTimeResponseTest {

    @Test
    public void testDeserialization() {
        String json = "{\"date\":\"15.07.1999\",\"time\":\"20:00\",\"weekday\":\"Donnerstag\"}";
        DateTimeResponse response = new Gson().fromJson(json, DateTimeResponse.class);

        assertEquals("15.07.1999", response.date);
        assertEquals("20:00", response.time);
        assertEquals("Donnerstag", response.weekday);
    }
}