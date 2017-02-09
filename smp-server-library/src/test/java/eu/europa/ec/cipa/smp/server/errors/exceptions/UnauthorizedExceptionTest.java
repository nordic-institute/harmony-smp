package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnauthorizedExceptionTest {

    @Test
    public void testUnauthorizedExceptionThrown() {
        // given

        // when
        try {
            throwUnauthorizedException();
        } catch(UnauthorizedException ex) {
            // then
            assertEquals("Exception thrown", ex.getMessage());
            return;
        }
        fail();
    }

    private void throwUnauthorizedException() {
        throw new UnauthorizedException("Exception thrown");
    }
}
