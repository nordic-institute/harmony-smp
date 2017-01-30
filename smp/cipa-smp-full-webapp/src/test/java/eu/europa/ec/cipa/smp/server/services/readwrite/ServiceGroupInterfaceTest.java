package eu.europa.ec.cipa.smp.server.services.readwrite;

import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.security.BlueCoatClientCertificateAuthentication;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.cipa.smp.server.security.UserRole.ROLE_ANONYMOUS;
import static eu.europa.ec.cipa.smp.server.security.UserRole.ROLE_SMP_ADMIN;

/**
 * Created by gutowpa on 30/01/2017.
 */
public class ServiceGroupInterfaceTest {

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
        String clientCertHeader = "serial=0000000000000123&subject=CN=SMP_7,O=DG-DIGIT,C=X&validFrom=Oct 21 02:00:00 2014 CEST&validTo=Oct 21 01:59:59 2018 CEST&issuer=CN=PEPPOL,O=X,C=Y";
        SecurityContextHolder.getContext().setAuthentication(new BlueCoatClientCertificateAuthentication(clientCertHeader));

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

    @Test(expected = XmlInvalidAgainstSchemaException.class) // WHITE-BOX test. XML validation error means that security checkup passed positively
    public void testSMPAdminAuthenticated() throws Throwable {
        //given
        List<GrantedAuthority> authorities = Arrays.<GrantedAuthority>asList(new SimpleGrantedAuthority(ROLE_SMP_ADMIN.name()));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(ANY_VALUE, ANY_VALUE, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when-then
        new ServiceGroupInterface().saveServiceGroup(ANY_VALUE, ANY_VALUE);
    }
}
