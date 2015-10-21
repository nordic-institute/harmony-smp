package eu.europa.ec.cipa.bdmsl.business.impl;

import eu.europa.ec.cipa.bdmsl.business.ICertificateDomainBusiness;
import eu.europa.ec.cipa.bdmsl.business.IX509CertificateBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.bdmsl.common.exception.GenericTechnicalException;
import eu.europa.ec.cipa.common.business.AbstractBusinessImpl;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Component
public class X509CertificateBusinessImpl extends AbstractBusinessImpl implements IX509CertificateBusiness {

    @Autowired
    private ICertificateDomainBusiness certificateDomainBusiness;

    @Override
    public String getTrustedRootCertificateDN(X509Certificate[] certificates) throws TechnicalException {
        String trustedRootDN = null;
        Collection<LdapName> trustedRootAliasCollection;
        try {
            trustedRootAliasCollection = CollectionUtils.collect(certificateDomainBusiness.findAll(), new Transformer() {
                public Object transform(Object o) {
                    try {
                        return new LdapName(((CertificateDomainBO) o).getRootCertificateAlias());
                    } catch (InvalidNameException e) {
                        throw new RuntimeException("Can not analyze the DN of the trusted root alias " + ((CertificateDomainBO) o).getRootCertificateAlias(), e);
                    }
                }
            });
        } catch (Exception exc) {
            throw new GenericTechnicalException(exc.getMessage(), exc);
        }

        // sometimes the order of the Rdn is reversed so we compare the Rdn, whatever the order
        for (X509Certificate cert : certificates) {
            try {
                for (LdapName trustedRootAlias : trustedRootAliasCollection) {
                    if (CollectionUtils.isEqualCollection(new LdapName(cert.getSubjectX500Principal().toString()).getRdns(), trustedRootAlias.getRdns())) {
                        trustedRootDN = cert.getSubjectX500Principal().toString();
                    } else if (CollectionUtils.isEqualCollection(new LdapName(cert.getIssuerX500Principal().toString()).getRdns(), trustedRootAlias.getRdns())) {
                        trustedRootDN = cert.getIssuerX500Principal().toString();
                    }
                }
            } catch (InvalidNameException e) {
                throw new GenericTechnicalException("Can not analyze the DN of the issuer or subject for the certificate of serial number " + cert.getSerialNumber(), e);
            }
        }
        return trustedRootDN;
    }

    @Override
    public X509Certificate getCertificate(final X509Certificate[] requestCerts) throws TechnicalException, BusinessException {
        if (requestCerts == null || requestCerts.length == 0) {
            // Empty array
            return null;
        }

        // Find all certificates that are not issuer to another certificate
        final List<X509Certificate> nonIssuerCertList = new ArrayList<X509Certificate>();
        for (final X509Certificate requestCert : requestCerts) {
            final X500Principal subject = requestCert.getSubjectX500Principal();

            // Search for the issuer of the current certificate
            boolean found = false;
            for (final X509Certificate issuerCert : requestCerts)
                if (subject.equals(issuerCert.getIssuerX500Principal())) {
                    found = true;
                    break;
                }
            if (!found)
                nonIssuerCertList.add(requestCert);
        }

        // Do we have exactly 1 certificate to verify?
        if (nonIssuerCertList.size() != 1)
            throw new CertificateAuthenticationException("Found " +
                    nonIssuerCertList.size() +
                    " certificates that are not issuer certificates!");

        final X509Certificate nonIssuerCert = nonIssuerCertList.get(0);
        return nonIssuerCert;
    }

    @Override
    public String calculateCertificateId(final X509Certificate cert) throws TechnicalException {
        // subject principal name must be in the order CN=XX,O=YY,C=ZZ
        // In some JDK versions it is O=YY,CN=XX,C=ZZ instead (e.g. 1.6.0_45)
        try {
            final LdapName ldapName = new LdapName(cert.getSubjectX500Principal().getName());

            // Make a map from type to name
            final Map<String, Rdn> parts = new HashMap<>();
            for (final Rdn rdn : ldapName.getRdns()) {
                parts.put(rdn.getType(), rdn);
            }

            // Re-order - least important item comes first (=reverse order)!
            List<Rdn> list = new ArrayList<>();
            list.add(parts.get("C"));
            list.add(parts.get("O"));
            list.add(parts.get("CN"));
            final String subjectName = new LdapName(list).toString();

            // subject-name + ":" + serial number hexstring
            String serialNumber = StringUtils.leftPad(cert.getSerialNumber().toString(), 16, "0");
            return subjectName + ':' + serialNumber;
        } catch (final Exception exc) {
            throw new CertificateAuthenticationException("Impossible to calculate the certificate Id of certificate " + cert.getSubjectX500Principal(), exc);
        }
    }
}
