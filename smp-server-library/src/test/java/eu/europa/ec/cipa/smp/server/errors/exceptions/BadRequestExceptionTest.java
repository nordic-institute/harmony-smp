package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.XSD_INVALID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by migueti on 16/01/2017.
 */
public class BadRequestExceptionTest {

    @Test
    public void testBadRequestExceptionThrown() {
        // given

        // when
        try {
            throwBadRequestException();
        } catch(BadRequestException ex) {
            // then
            assertEquals(XSD_INVALID, ex.getErrorBusinessCode());
            assertEquals("Exception thrown", ex.getMessage());
            return;
        }
        fail();
    }

    private void throwBadRequestException() {
        throw new BadRequestException(XSD_INVALID, "Exception thrown");
    }

}
