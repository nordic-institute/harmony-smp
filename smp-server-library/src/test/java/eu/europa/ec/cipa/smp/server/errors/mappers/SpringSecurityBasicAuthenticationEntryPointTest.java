package eu.europa.ec.cipa.smp.server.errors.mappers;

import com.helger.web.mock.MockHttpServletRequest;
import com.helger.web.mock.MockHttpServletResponse;
import eu.europa.ec.cipa.smp.server.errors.SpringSecurityBasicAuthenticationEntryPoint;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gutowpa on 30/01/2017.
 */
public class SpringSecurityBasicAuthenticationEntryPointTest {

    @Test
    public void test() throws IOException, ServletException {
        //given
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        AuthenticationException exception = new BadCredentialsException("wrong password");
        SpringSecurityBasicAuthenticationEntryPoint entryPoint = new SpringSecurityBasicAuthenticationEntryPoint();

        //when
        entryPoint.commence(req, resp, exception);

        //then
        assertEquals("text/xml", resp.getContentType());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("<ErrorResponse xmlns=\"ec:services:SMP:1.0\"><BusinessCode>UNAUTHORIZED</BusinessCode><ErrorDescription>wrong password</ErrorDescription><ErrorUniqueId>"));
    }
}
