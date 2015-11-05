package eu.europa.ec.cipa.bdmsl.business.impl;

import eu.europa.ec.cipa.bdmsl.business.ICertificateDomainBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDomainDAO;
import eu.europa.ec.cipa.common.business.AbstractBusinessImpl;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Component
public class CertificateDomainBusinessImpl extends AbstractBusinessImpl implements ICertificateDomainBusiness {

    @Autowired
    private ICertificateDomainDAO certificateDomainDAO;

    @Override
    public CertificateDomainBO findDomain(String rootCertificateAlias) throws TechnicalException {
        // This method
        List<CertificateDomainBO> certificateDomainBOs = certificateDomainDAO.findAll();

        String orderedRootCertificateAlias = order(rootCertificateAlias);

        for (CertificateDomainBO certificateDomainBO : certificateDomainBOs) {
            if (order(certificateDomainBO.getRootCertificateAlias()).equalsIgnoreCase(orderedRootCertificateAlias)) {
                return certificateDomainBO;
            }
        }
        return null;

    }

    private String order(String certificate) throws TechnicalException  {
        String orderedCertificate = "";
        final LdapName ldapName;
        try {
            ldapName = new LdapName(certificate);
            // Make a map from type to name
            Map<String, Rdn> parts = new HashMap<>();
            for (final Rdn rdn : ldapName.getRdns()) {
                parts.put(rdn.getType(), rdn);
            }
            // The treemap orders the keys according to the natural ordering. The keys are strings so the order is alphabetical.
            Map<String, Rdn> treeMap = new TreeMap<>(parts);
            int i = 0;
            for (String key : treeMap.keySet()) {
                orderedCertificate += treeMap.get(key);
                if (i != treeMap.keySet().size() - 1) {
                    orderedCertificate += ",";
                }
                i++;
            }

        } catch (InvalidNameException exc) {
            throw new CertificateAuthenticationException("Impossible to re-order the root certificate of the domain " + certificate, exc);
        }

        return orderedCertificate;
    }

    @Override
    public List<CertificateDomainBO> findAll() throws TechnicalException {
        return certificateDomainDAO.findAll();
    }
}
