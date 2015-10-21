package eu.europa.ec.cipa.bdmsl.security;

import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by feriaad on 17/06/2015.
 */
public class X509CertificateAuthentication implements Authentication {

    private boolean authenticated;
    private String certificateId;
    private X509Certificate[] certificates;
    private CertificateDetails details;
    private Collection<GrantedAuthority> authorityList;

    public X509CertificateAuthentication(final X509Certificate[] certificates, X509Certificate clientCertificate, final String certificateId) throws TechnicalException, BusinessException {
        this.certificates = certificates;
        this.certificateId = certificateId;
        details = new CertificateDetails();
        details.setValidFrom(DateUtils.toCalendar(clientCertificate.getNotBefore()));
        details.setValidTo(DateUtils.toCalendar(clientCertificate.getNotAfter()));
        details.setSerial(StringUtils.leftPad(clientCertificate.getSerialNumber().toString(), 16, "0"));
        details.setIssuer(clientCertificate.getIssuerDN().toString());
        details.setPemEncoding(convertToBase64PEMString(clientCertificate));
        this.authorityList = Collections.unmodifiableList(retrieveAuthorities(clientCertificate));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    private List<? extends GrantedAuthority> retrieveAuthorities(X509Certificate clientCertificate) throws CertificateAuthenticationException {
        LdapName ldapName;
        try {
            ldapName = new LdapName(clientCertificate.getSubjectX500Principal().getName());
        } catch (InvalidNameException exc) {
            throw new CertificateAuthenticationException("Impossible to identify authorities for certificate " + clientCertificate.getSubjectX500Principal(), exc);
        }

        String commonName = null;
        for (final Rdn rdn : ldapName.getRdns()) {
            if ("CN".equals(rdn.getType())) {
                commonName = (String) rdn.getValue();
            }
        }

        List<GrantedAuthority> roles = new ArrayList<>();

        if (commonName.startsWith("SMP_")) {
            // SMP certificate --> SMP_ROLE
            roles.add(new SimpleGrantedAuthority(CustomAuthenticationProvider.SMP_ROLE));
        } else if (commonName.startsWith("PYP_")) {
            // PYP certificate --> PYP_ROLE
            roles.add(new SimpleGrantedAuthority(CustomAuthenticationProvider.PYP_ROLE));
        } else {
            roles = AuthorityUtils.NO_AUTHORITIES;
        }
        return roles;
    }

    @Override
    public Object getCredentials() {
        return certificates;
    }

    @Override
    public Object getDetails() {
        return details;
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
     * Converts a {@link X509Certificate} instance into a Base-64 encoded string (PEM format).
     *
     * @param x509Cert A X509 Certificate instance
     * @return PEM formatted String
     * @throws IOException
     */
    public String convertToBase64PEMString(X509Certificate x509Cert) throws CertificateAuthenticationException {
        try {
            StringWriter sw = new StringWriter();
            try (JcaPEMWriter pw = new JcaPEMWriter(sw)) {
                pw.writeObject(x509Cert);
            }
            return sw.toString();
        }catch (final IOException exc) {
            throw new CertificateAuthenticationException("Unable to get the PEM encoded string for certificate " + x509Cert.getSubjectX500Principal().toString(), exc);
        }
    }
}
