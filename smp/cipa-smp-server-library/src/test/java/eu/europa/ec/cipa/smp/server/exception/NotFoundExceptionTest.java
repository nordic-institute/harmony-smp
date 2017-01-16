package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class NotFoundExceptionTest {

    @Test
    public void testCreateNewException() {
        // given

        // when
        NotFoundException exception = new NotFoundException("New Not Found Exception");

        // then
        assertNotNull(exception);
        assertEquals("New Not Found Exception", exception.getMessage());
    }
}
