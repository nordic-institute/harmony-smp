/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
