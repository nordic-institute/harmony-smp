package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnknownUserExceptionTest {

    @Test
    public void testUnknownUserExceptionThrown() {
        // given

        // when
        try {
            thrownUnknownUserException();
        } catch(UnknownUserException ex) {
            // then
            assertEquals("unknown_user", ex.getUserName());
            assertEquals("Unknown user 'unknown_user'", ex.getMessage());
            return;
        }
        fail();
    }

    private void thrownUnknownUserException() {
        throw new UnknownUserException("unknown_user");
    }
}
