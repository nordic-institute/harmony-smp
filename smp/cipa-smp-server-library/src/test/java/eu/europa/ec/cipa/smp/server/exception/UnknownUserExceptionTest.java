package eu.europa.ec.cipa.smp.server.exception;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 16/01/2017.
 */
public class UnknownUserExceptionTest {

    private UnknownUserException exception;

    @Before
    public void setup() {
        // given
        exception = new UnknownUserException("unknown");
    }

    @Test
    public void testCreateNewException() {
        // when
        // calls setup

        // then
        assertNotNull(exception);
        Assert.assertEquals("Unknown user 'unknown'", exception.getMessage());
    }

    @Test
    public void testGetUserName() {
        // when
        // calls setup and
        String testUser = exception.getUserName();

        // then
        assertEquals("unknown", testUser);
    }
}
