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

package eu.europa.ec.cipa.smp.server.security;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Pawel Gutowski on 05/04/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class BlueCoatAuthenticationFilterTest {

    private static final Object[] positiveTestCases() {
        return new Object[][]{
                {
                        "regular positive scenario",
                        "sno%3De6%3A66%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                        "CN=common name,O=org,C=BE",
                        "CN=issuer common name,O=issuer org,C=DE",
                        "000000000000e666"
                },
                {
                        "regular positive scenario with changed values",
                        "sno%3De6%3A77%26subject%3DC%3DPL,O%3Dsubject%20org,CN%3Dsubject%20common%20name%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DFR,O%3Dorg,CN%3Dcommon%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                        "CN=subject common name,O=subject org,C=PL",
                        "CN=common name,O=org,C=FR",
                        "000000000000e677"
                },
                {
                        "HTML characters (ampersand) are unescaped correctly",
                        "sno%3De6%3A66%26amp%3Bsubject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26amp%3Bvalidfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26amp%3Bvalidto%3DJul++9+23%3A59%3A00+2019+GMT%26amp%3Bissuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26amp%3Bpolicy_oids%3D1.3.6.1.4.1.7879.13.25",
                        "CN=common name,O=org,C=BE",
                        "CN=issuer common name,O=issuer org,C=DE",
                        "000000000000e666"
                },
                {
                        "fields order does not matter and non-used fields are skipped",
                        "validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26sno%3De6%3A66",
                        "CN=common name,O=org,C=BE",
                        "CN=issuer common name,O=issuer org,C=DE",
                        "000000000000e666"
                },
                {
                        "serial number can be represent by different key",
                        "serial%3De6%3A66%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                        "CN=common name,O=org,C=BE",
                        "CN=issuer common name,O=issuer org,C=DE",
                        "000000000000e666"
                },
                {
                        "keys are case insensitive",
                        "Sno%3De6%3A66%26suBJEct%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26ISSuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                        "CN=common name,O=org,C=BE",
                        "CN=issuer common name,O=issuer org,C=DE",
                        "000000000000e666"
                },
                {
                        "garbage is skipped - emailAddress added to the end of CN",
                        "sno%3De6%3A66%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE/emailAddress\\=issuer@ec.europa.eu,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                        "CN=common name,O=org,C=BE",
                        "CN=issuer common name,O=issuer org,C=DE",
                        "000000000000e666"
                },
                {
                        "garbage is skipped - emailAddress added to the end of CN, but not at the end of subject",
                        "sno=f7%3A1e%3Ae8%3Ab1%3A1c%3Ab3%3Ab7%3A87&subject=C=BE, O=European Commission, OU=CEF_eDelivery.europa.eu,+OU=eHealth,+OU=SMP_TEST,+CN=EHEALTH_SMP_EC/emailAddress=CEF-EDELIVERY-SUPPORT@ec.europa.eu&validfrom=Dec++6+17%3A41%3A42+2016+GMT&validto=Jul++9+23%3A59%3A00+2019+GMT&issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&policy_oids=1.3.6.1.4.1.7879.13.25",
                        "CN=EHEALTH_SMP_EC,O=European Commission,C=BE",
                        "CN=Shared Business CA 4,O=T-Systems International GmbH,C=DE",
                        "f71ee8b11cb3b787"
                }
        };
    }

    private static final Object[] negativeTestCases() {
        return new Object[][]{
                {
                        "malformed header value",
                        "this is malformed header value that results in authentication error"
                },
                {
                        "missing mandatory field serial number",
                        "subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                },
                {
                        "missing mandatory field subject",
                        "sno%3De6%3A66%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                },
                {
                        "missing mandatory field issuer",
                        "sno%3De6%3A66%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26policy_oids%3D1.3.6.1.4.1.7879.13.25",
                },
                {
                        "certificate not yet valid",
                        "sno%3De6%3A66%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2050+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25"
                },
                {
                        "certificate expired",
                        "sno%3De6%3A66%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2050+GMT%26validto%3DJul++9+23%3A59%3A00+2016+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25"
                }
        };
    }

    @After
    @Before
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Parameters(method = "positiveTestCases")
    @TestCaseName("{0}")
    public void correctHeaderResultsInPositiveAuthentication(String caseName, String header, String expSubjectDN, String expIssuerDN, String expSerial) throws IOException, ServletException {
        //given-when
        String expName = expSubjectDN + ":" + expSerial;
        runAuth(true, header);

        //then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(expName, auth.getName());
        assertEquals("N/A", auth.getCredentials());

        Object details = auth.getDetails();
        assertTrue(auth.getPrincipal() instanceof PreAuthenticatedCertificatePrincipal);
        PreAuthenticatedCertificatePrincipal principal = (PreAuthenticatedCertificatePrincipal) auth.getPrincipal();
        assertEquals(details, principal);
        assertEquals(expSubjectDN, principal.getSubjectDN());
        assertEquals(expIssuerDN, principal.getIssuerDN());
        assertEquals(expName, principal.getName());
    }

    @Test
    @Parameters(method = "negativeTestCases")
    @TestCaseName("{0}")
    public void invalidHeaderValueThrowsAuthException(String caseName, String header) throws IOException, ServletException {
        //given-when
        runAuth(true, header);

        //then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void blueCoatTurnedOffByDefault() throws IOException, ServletException {
        //given-when
        String correctHeader = "sno%3De6%3A66%26subject%3DC%3DBE,O%3Dorg,CN%3Dcommon%20name%26validfrom%3DFeb++1+14%3A20%3A18+2017+GMT%26validto%3DJul++9+23%3A59%3A00+2019+GMT%26issuer%3DC%3DDE,O%3Dissuer%20org,CN%3Dissuer%20common%20name%26policy_oids%3D1.3.6.1.4.1.7879.13.25";
        runAuth(false, correctHeader);

        //then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void missingHeaderIsIgnored() throws IOException, ServletException {
        //given-when
        runAuth(true, null);

        //then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void runAuth(boolean blueCoatActive, String headerValue) throws IOException, ServletException {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (headerValue != null) {
            request.addHeader("Client-Cert", headerValue);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        BlueCoatAuthenticationFilter filter = new BlueCoatAuthenticationFilter();
        if (blueCoatActive) {
            filter.setBlueCoatEnabled(true);
        }
        filter.setAuthenticationManager(createAuthenticationManager());

        //when
        filter.doFilter(request, response, chain);
    }

    /**
     * Create an authentication manager which returns the passed in object.
     */
    private AuthenticationManager createAuthenticationManager() {
        AuthenticationManager am = mock(AuthenticationManager.class);
        when(am.authenticate(any(Authentication.class))).thenAnswer(
                new Answer<Authentication>() {
                    public Authentication answer(InvocationOnMock invocation) throws Throwable {
                        return (Authentication) invocation.getArguments()[0];
                    }
                });
        return am;
    }
}
