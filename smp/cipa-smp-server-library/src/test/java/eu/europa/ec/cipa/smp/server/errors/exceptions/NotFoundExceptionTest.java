package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by migueti on 16/01/2017.
 */
public class NotFoundExceptionTest {

    @Test
    public void testNotFoundExceptionThrown() {
        // given

        // when
        try {
            throwNotFoundException();
        } catch (NotFoundException ex) {
            // then
            assertEquals("Exception thrown", ex.getMessage());
            return;
        }
        fail();
    }

    private void throwNotFoundException() {
        throw new NotFoundException("Exception thrown");
    }
}
