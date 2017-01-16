package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class XmlParsingExceptionTest {

    @Test
    public void testCreateNewException() {
        // given
        Exception parent = new Exception("Parent Exception");

        // when
        XmlParsingException exception = new XmlParsingException(parent);

        // then
        assertNotNull(exception);
        assertEquals("java.lang.Exception: Parent Exception", exception.getMessage());
    }
}
