package eu.europa.ec.cipa.smp.server.errors.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by migueti on 16/01/2017.
 */
public class XmlParsingExceptionTest {

    @Test
    public void testXmlParsingExceptionThrown() {
        // given

        // when
        try {
            throwXmlParsingException();
        } catch(XmlParsingException ex) {
            // then
            assertEquals("java.lang.Exception: Parent Exception", ex.getMessage());
            return;
        }
        fail();
    }

    private void throwXmlParsingException() {
        throw new XmlParsingException(new Exception("Parent Exception"));
    }
}
