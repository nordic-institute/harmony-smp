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

package eu.europa.ec.cipa.smp.server.services.readwrite;

import eu.europa.ec.cipa.smp.server.errors.exceptions.BadRequestException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.util.DefaultHttpHeader;
import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.cipa.smp.server.security.UserRole.ROLE_ANONYMOUS;
import static eu.europa.ec.cipa.smp.server.security.UserRole.ROLE_SMP_ADMIN;
import static junit.framework.TestCase.fail;

/**
 * Created by gutowpa on 30/01/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext.xml")
@WebAppConfiguration
public class ServiceGroupInterfaceTest {

    private static final String ANY_VALUE = "just any random value";

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() throws ServletException {
        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(wac);
        ServletContextEvent event = new ServletContextEvent(sc);
        listener.contextInitialized(event);
    }

    @Test(expected = UnauthorizedException.class)
    public void testNotAuthenticated() throws Throwable {
        //given
        SecurityContextHolder.getContext().setAuthentication(null);

        //when-then
        new ServiceGroupInterface().saveServiceGroup(ANY_VALUE, ANY_VALUE);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAnonymousAuthenticated() throws Throwable {
        //given
        List<GrantedAuthority> authorities = Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority(ROLE_ANONYMOUS.name()));
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(ANY_VALUE, ANY_VALUE, authorities));

        //when-then
        new ServiceGroupInterface().saveServiceGroup(ANY_VALUE, ANY_VALUE);
    }

    @Test(expected = UnauthorizedException.class)
    public void testBlueCoatAuthenticated() throws Throwable {
        //given
        Principal principal = new PreAuthenticatedCertificatePrincipal("CN=SMP_7,O=DG-DIGIT,C=X", "CN=PEPPOL,O=X,C=Y", "123");
        Authentication authentication = new PreAuthenticatedAuthenticationToken(principal, ANY_VALUE);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when-then
        new ServiceGroupInterface().saveServiceGroup(ANY_VALUE, ANY_VALUE);
    }

    @Test(expected = UnauthorizedException.class)
    public void testNotSMPAdminAuthenticated() throws Throwable {
        //given
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(ANY_VALUE, ANY_VALUE));

        //when-then
        new ServiceGroupInterface().saveServiceGroup(ANY_VALUE, ANY_VALUE);
    }

    @Test(expected = XmlInvalidAgainstSchemaException.class)
    // WHITE-BOX test. XML validation error means that security checkup passed positively
    public void testSMPAdminAuthenticated() throws Throwable {
        //given
        authenticateAsSmpAdmin();

        //when-then
        new ServiceGroupInterface().saveServiceGroup(ANY_VALUE, ANY_VALUE);
    }

    private void authenticateAsSmpAdmin() {
        List<GrantedAuthority> authorities = Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority(ROLE_SMP_ADMIN.name()));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(ANY_VALUE, ANY_VALUE, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testServiceGroupIdentifierSchemeValidationPositive() throws Throwable {

        //given
        String scheme = "char4-number00-allowed6";
        String particpantID = getSampleParticipantIdentifierWithScheme(scheme);
        String body = getSampleServiceGroupBodyWithScheme(scheme);

        authenticateAsSmpAdmin();

        ServiceGroupInterface serviceGroupInterface = new ServiceGroupInterface();
        ReflectionTestUtils.setField(serviceGroupInterface, "headers", new DefaultHttpHeader());

        //when-then
        try {
            serviceGroupInterface.saveServiceGroup(particpantID, body);
        } catch (UnauthorizedException e) {
            // TODO update these assertions after migrating to Spring
            // Whitebox
            // This message comes from code AFTER successfull validation of input data
            Assert.assertEquals("Missing required HTTP header 'Authorization' for user authentication", e.getMessage());
            return;
        }
        fail("ServiceGroup Identifier scheme should have fail regexp validation");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationTooLong() throws Throwable {
        validateBadScheme("length-exceeeeeeds-25chars");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationNotBuiltWithThreeSegments() throws Throwable {
        validateBadScheme("too-many-segments-inside");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationTooLittleSegments() throws Throwable {
        validateBadScheme("only-two");
    }

    @Test(expected = BadRequestException.class)
    public void testServiceGroupIdentifierSchemeValidationIllegalChar() throws Throwable {
        validateBadScheme("illegal-char-here:");
    }

    private void validateBadScheme(String scheme) throws Throwable {
        //given
        String particpantID = getSampleParticipantIdentifierWithScheme(scheme);
        String body = getSampleServiceGroupBodyWithScheme(scheme);
        authenticateAsSmpAdmin();

        //when-then
        new ServiceGroupInterface().saveServiceGroup(particpantID, body);
    }

    private String getSampleParticipantIdentifierWithScheme(String scheme) {
        return scheme + "::urn:poland:ncpb";
    }

    private String getSampleServiceGroupBodyWithScheme(String scheme) {
        return String.format("<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
                "   <ParticipantIdentifier scheme=\"%s\">urn:poland:ncpb</ParticipantIdentifier>\n" +
                "   <ServiceMetadataReferenceCollection/>\n" +
                " </ServiceGroup>", scheme);
    }
}
