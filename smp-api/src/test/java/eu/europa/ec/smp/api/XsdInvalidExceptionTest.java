package eu.europa.ec.smp.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by migueti on 23/01/2017.
 */
public class XsdInvalidExceptionTest {

    @Test(expected = XsdInvalidException.class)
    public void testXsdInvalidExceptionThrown() throws XsdInvalidException {
        // given

        // when
        try {
            throwXsdInvalidException();
        } catch (XsdInvalidException ex) {
            // then
            assertEquals("Invalid test element", ex.getMessage());
            throw ex;
        }
    }

    private void throwXsdInvalidException() throws XsdInvalidException {
        throw new XsdInvalidException("Invalid test element");
    }
}
