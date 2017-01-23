package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.exception.AuthenticationException;
import eu.europa.ec.cipa.smp.server.util.CertificateUtils;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Blue Coat is the name of the reverse proxy at the commission.
 * It forwards the request in HTTP with the certificate details inside the request.
 * This class extracts the data from the header.
 * Created by feriaad on 17/06/2015.
 */
public class BlueCoatClientCertificateAuthentication implements Authentication {


    private boolean authenticated;
    private String certificateId;
    private CertificateDetails certificate;
    private Collection<GrantedAuthority> authorityList;


    public BlueCoatClientCertificateAuthentication(final String certHeaderValue) throws AuthenticationException {
        certificate = new CertificateDetails();
        this.certificateId = calculateCertificateIdFromHeader(certHeaderValue);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public Object getCredentials() {
        return certificate;
    }

    @Override
    public Object getDetails() {
        return certificate;
    }

    @Override
    public Object getPrincipal() {
        return certificateId;
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
        return certificateId;
    }

    /**
     * This method calculates the certificate id from the certificate's header
     *
     * @param certHeaderValue Certificate's header
     * @return Certificate Id
     * @throws AuthenticationException Certificate Authentication Exception
     */
    private String calculateCertificateIdFromHeader(final String certHeaderValue) throws AuthenticationException {
        try {
            certificate = CertificateUtils.getCommonNameFromCalculateHeaderCertificateId(certHeaderValue);
            return certificate.getCertificateId();
        } catch (final Exception exc) {
            throw new AuthenticationException("Impossible to determine the certificate identifier from " + certHeaderValue, exc);
        }
    }

    /**
     * Retrieves authorities from Certificate's Header
     *
     * @param certHeaderValue Certificate's Header
     * @return List of Granted Authorities
     * @throws AuthenticationException Certificate Authentication Exception
     */
    private List<? extends GrantedAuthority> retrieveAuthorities(String certHeaderValue) throws AuthenticationException {
        throw new NotImplementedException("NotImplementedException");
    }
}
