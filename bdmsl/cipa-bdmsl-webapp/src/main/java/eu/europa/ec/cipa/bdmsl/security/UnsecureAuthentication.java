package eu.europa.ec.cipa.bdmsl.security;

import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.common.util.Constant;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Created by feriaad on 17/06/2015.
 */
public class UnsecureAuthentication implements Authentication {

    private boolean authenticated;

    public static final String UNSECURE_HTTP_CLIENT = "unsecure-http-client";

    private CertificateDetails certificate;

    public UnsecureAuthentication() throws TechnicalException {
        certificate = new CertificateDetails();
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Constant.LOCALE);
        try {
            Date validFrom = df.parse("Jan 01 00:00:00 1970 CEST");
            Date validTo = df.parse("Dec 31 23:59:59 2999 CEST");
            certificate.setValidFrom(DateUtils.toCalendar(validFrom));
            certificate.setValidTo(DateUtils.toCalendar(validTo));
            certificate.setIssuer("unsecure-issuer");
            certificate.setSerial("unsecure-serial");
            certificate.setSubject(UNSECURE_HTTP_CLIENT);
            certificate.setRootCertificateDN("CN=unsecure_root,O=delete_in_production,C=only_for_testing");
        } catch (ParseException e) {
            throw new CertificateAuthenticationException("Couldn't authenticate the unsecure user", e);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority[] {new SimpleGrantedAuthority(CustomAuthenticationProvider.SMP_ROLE), new SimpleGrantedAuthority(CustomAuthenticationProvider.PYP_ROLE), new SimpleGrantedAuthority(CustomAuthenticationProvider.ADMIN_ROLE)});
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return certificate;
    }

    @Override
    public Object getPrincipal() {
        return UNSECURE_HTTP_CLIENT;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public String getName() {
        return UNSECURE_HTTP_CLIENT;
    }

}
