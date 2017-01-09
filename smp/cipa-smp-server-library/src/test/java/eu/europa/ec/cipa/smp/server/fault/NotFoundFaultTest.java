package eu.europa.ec.cipa.smp.server.fault;

import com.sun.jersey.api.NotFoundException;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 09/01/2017.
 */
public class NotFoundFaultTest {

    @Test
    public void testNotFoundFault() {
        assertNotNull(new NotFoundFault());
    }

    @Test
    public void testNotFoundFaultMessage() {
        Exception result = new NotFoundFault("test message");
        assertNotNull(result);
        assertEquals("test message", result.getMessage());
    }

    @Test
    public void testNotFoundFaultMessageAndThrowable() {
        NotFoundException exception = Mockito.mock(NotFoundException.class);
        Exception result = new NotFoundFault("test message", exception);
        assertNotNull(result);
        assertEquals("test message", result.getMessage());
        assertEquals(exception.getClass(), result.getCause().getClass());
    }

    @Test
    public void testNotFoundMessageAndUuid() {
        Exception result = new NotFoundFault("test message", "uuid");
        assertNotNull(result);
        assertEquals("uuid: test message", result.getMessage());
    }
}
