package com.philipp.dv_projekt;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerResponseHandlerTest {

    private ServerResponseHandler handler;

    @Before
    public void setUp() {
        handler = new ServerResponseHandler();
    }

    @Test
    public void testAudioGenerationFailure() {
        String json = "{\"type\":\"AUDIO_GENERATION_REQUEST_FAILURE\", \"message\":\"Fehlermeldung\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.AUDIO_GENERATION_REQUEST_FAILURE, result.getType());
        assertEquals("Fehlermeldung", result.getMessage());
    }

    @Test
    public void testAudioGenerationFailureWithoutMessage() {
        String json = "{\"type\":\"AUDIO_GENERATION_REQUEST_FAILURE\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.FAILURE, result.getType());
        assertEquals("Keine Message in AUDIO_GENERATION_REQUEST_FAILURE vorhanden", result.getMessage());
    }

    @Test
    public void testAudioGenerationSuccess() {
        String json = "{\"type\":\"AUDIO_GENERATION_REQUEST_SUCCESS\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.AUDIO_GENERATION_REQUEST_SUCCESS, result.getType());
    }

    @Test
    public void testExtractDataFromAudioSuccess() {
        String json = "{\"type\":\"EXTRACT_DATA_FROM_AUDIO_SUCCESS\", \"message\":\"NO\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.EXTRACT_DATA_FROM_AUDIO_SUCCESS, result.getType());
    }

    @Test
    public void testFailureWithMessage() {
        String json = "{\"type\":\"FAILURE\", \"message\":\"Fehlermeldung\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.FAILURE, result.getType());
        assertEquals("Fehlermeldung", result.getMessage());
    }

    @Test
    public void testFailureWithoutMessage() {
        String json = "{\"type\":\"FAILURE\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.FAILURE, result.getType());
        assertEquals("Keine Message vorhanden", result.getMessage());
    }

    @Test
    public void testKnownCustomerWithAppointment() {
        String json = "{\"type\":\"KNOWN_CUSTOMER\", \"appointment\":true}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.KNOWN_CUSTOMER, result.getType());
    }

    @Test
    public void testKnownCustomerWithoutAppointment() {
        String json = "{\"type\":\"KNOWN_CUSTOMER\", \"appointment\":false}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.KNOWN_CUSTOMER_WITHOUT_APPOINTMENT, result.getType());
    }

    @Test
    public void testKnownCustomerAppointmentMissing() {
        String json = "{\"type\":\"KNOWN_CUSTOMER\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.FAILURE, result.getType());
        assertEquals("appointment fehlt in KNOWN_CUSTOMER", result.getMessage());
    }

    @Test
    public void testNextAppointment() {
        String json = "{\"type\":\"NEXT_APPOINTMENT\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.NEXT_APPOINTMENT, result.getType());
    }

    @Test
    public void testPersonDataSuccess() {
        String json = "{\"type\":\"PERSON_DATA\", \"success\":success}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.PERSON_DATA, result.getType());
    }

    @Test
    public void testPersonDataFailure() {
        String json = "{\"type\":\"PERSON_DATA\", \"success\":false}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.PERSON_DATA_SUCCESS_FALSE, result.getType());
    }

    @Test
    public void testPersonDataMissingSuccess() {
        String json = "{\"type\":\"PERSON_DATA\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.FAILURE, result.getType());
        assertEquals("success fehlt in PERSON_DATA", result.getMessage());
    }

    @Test
    public void testRobotReachedGoal() {
        String json = "{\"type\":\"ROBOT_REACHED_GOAL\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.ROBOT_REACHED_GOAL, result.getType());
    }

    @Test
    public void testTimeout() {
        String json = "{\"type\":\"TIMEOUT\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.FAILURE, result.getType());
        assertEquals("Timeout vom Server erhalten", result.getMessage());
    }

    @Test
    public void testUnknownCustomer() {
        String json = "{\"type\":\"UNKNOWN_CUSTOMER\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.UNKNOWN_CUSTOMER, result.getType());
    }

    @Test
    public void testUnknownType() {
        String json = "{\"type\":\"UNBEKANNT\"}";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.UNKNOWN_RESPONSE, result.getType());
        assertEquals("Type nicht vorhanden, oder noch nicht implementiert", result.getMessage());
    }

    @Test
    public void testMalformedJson() {
        String json = "Nicht gültiges JSON";
        ResponseResult result = handler.getResponseType(json);
        assertEquals(ResponseType.UNKNOWN_RESPONSE, result.getType());
        assertTrue(result.getMessage().startsWith("Exception beim JSON-Parsing"));
    }
}