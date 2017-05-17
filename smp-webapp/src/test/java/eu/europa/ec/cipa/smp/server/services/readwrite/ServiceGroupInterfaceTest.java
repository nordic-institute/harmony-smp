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

import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.cipa.smp.server.security.UserRole.ROLE_ANONYMOUS;
import static eu.europa.ec.cipa.smp.server.security.UserRole.ROLE_SMP_ADMIN;

/**
 * Created by gutowpa on 30/01/2017.
 */
public class ServiceGroupInterfaceTest{

    private static final String ANY_VALUE = "just any random value";

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
        List<GrantedAuthority> authorities = Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority(ROLE_SMP_ADMIN.name()));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(ANY_VALUE, ANY_VALUE, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when-then
        new ServiceGroupInterface().saveServiceGroup(ANY_VALUE, ANY_VALUE);
    }
}
