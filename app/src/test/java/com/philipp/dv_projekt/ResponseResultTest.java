package com.philipp.dv_projekt;

import org.junit.Test;
import static org.junit.Assert.*;


public class ResponseResultTest {

    @Test
    public void testConstructorAndGetters() {
        ResponseType type = ResponseType.AUDIO_GENERATION_REQUEST_FAILURE;
        String message = "Fehlermeldung";

        ResponseResult result = new ResponseResult(type, message);

        assertEquals(ResponseType.AUDIO_GENERATION_REQUEST_FAILURE, result.getType());
        assertEquals("Fehlermeldung", result.getMessage());
    }

    @Test
    public void testNullMessage() {
        ResponseResult result = new ResponseResult(ResponseType.UNKNOWN_RESPONSE, null);

        assertEquals(ResponseType.UNKNOWN_RESPONSE, result.getType());
        assertNull(null, result.getMessage());
    }
}
