package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Before;
import org.junit.Test;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.XSD_INVALID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class BadRequestExceptionTest {

    private BadRequestException exception;

    @Before
    public void setup() {
        // given

        // when
        exception = new BadRequestException(XSD_INVALID, "New Bad Request Exception");
    }

    @Test
    public void testCreateNewException() {
        // then
        assertNotNull(exception);
        assertEquals("New Bad Request Exception", exception.getMessage());
    }

    @Test
    public void testGetBusinnessCode() {
        // then
        assertEquals(XSD_INVALID, exception.getErrorBusinessCode());
    }
}
