package com.philipp.dv_projekt;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

public class PersonResponseTest {

    @Test
    public void testDeserialization() {
        String json = "{\n" +
                "  \"message\": {\n" +
                "    \"firstname\": \"Philipp\",\n" +
                "    \"lastname\": \"Hagel\",\n" +
                "    \"date_of_birth\": \"1999-07-15\",\n" +
                "    \"sex\": \"m\",\n" +
                "    \"phone_number\": \"1234567\",\n" +
                "    \"email_address\": \"philipp@familie-hagel.de\"\n" +
                "  }\n" +
                "}";

        Gson gson = new Gson();
        PersonResponse response = gson.fromJson(json, PersonResponse.class);

        assertEquals("Philipp", response.message.firstname);
        assertEquals("Hagel", response.message.lastname);
        assertEquals("m", response.message.sex);
        assertEquals("1999-07-15", response.message.date_of_birth);
        assertEquals("1234567", response.message.phone_number);
        assertEquals("philipp@familie-hagel.de", response.message.email_address);
    }
}

