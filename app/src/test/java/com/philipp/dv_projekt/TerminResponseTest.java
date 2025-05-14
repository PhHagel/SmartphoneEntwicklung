package com.philipp.dv_projekt;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

public class TerminResponseTest {

    @Test
    public void testDeserialization() {
        String json = "{\"message\":\"Ja\"}";
        Gson gson = new Gson();
        TerminResponse response = gson.fromJson(json, TerminResponse.class);

        assertEquals("Ja", response.message);
    }
}