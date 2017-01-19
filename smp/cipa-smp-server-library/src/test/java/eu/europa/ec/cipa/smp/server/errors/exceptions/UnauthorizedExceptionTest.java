package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnauthorizedExceptionTest {

    @Test
    public void testCreateNewException() {
        // given

        // when
        UnauthorizedException exception = new UnauthorizedException("New Unauthorized Exception");

        // then
        assertNotNull(exception);
        assertEquals("New Unauthorized Exception", exception.getMessage());
    }
}
