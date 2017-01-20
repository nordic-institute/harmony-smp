package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnknownUserExceptionTest {

    @Test(expected = UnknownUserException.class)
    public void testUnknownUserExceptionThrown() {
        // given

        // when
        try {
            thrownUnknownUserException();
        } catch(UnknownUserException ex) {
            // then
            assertEquals("unknown_user", ex.getUserName());
            assertEquals("Unknown user 'unknown_user'", ex.getMessage());
            throw ex;
        }
    }

    private void thrownUnknownUserException() {
        throw new UnknownUserException("unknown_user");
    }
}
