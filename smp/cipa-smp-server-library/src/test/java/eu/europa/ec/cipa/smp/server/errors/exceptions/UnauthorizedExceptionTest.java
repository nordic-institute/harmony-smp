package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnauthorizedExceptionTest {

    @Test(expected = UnauthorizedException.class)
    public void testUnauthorizedExceptionThrown() {
        // given

        // when
        try {
            throwUnauthorizedException();
        } catch(UnauthorizedException ex) {
            // then
            assertEquals("Exception thrown", ex.getMessage());
            throw ex;
        }
    }

    private void throwUnauthorizedException() {
        throw new UnauthorizedException("Exception thrown");
    }
}
