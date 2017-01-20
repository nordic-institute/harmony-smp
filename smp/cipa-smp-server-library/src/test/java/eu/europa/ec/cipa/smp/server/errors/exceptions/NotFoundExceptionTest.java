package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by migueti on 16/01/2017.
 */
public class NotFoundExceptionTest {

    @Test(expected = NotFoundException.class)
    public void testNotFoundExceptionThrown() {
        // given

        // when
        try {
            throwNotFoundException();
        } catch (NotFoundException ex) {
            // then
            assertEquals("Exception thrown", ex.getMessage());
            throw ex;
        }
    }

    private void throwNotFoundException() {
        throw new NotFoundException("Exception thrown");
    }
}
