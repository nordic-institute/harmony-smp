package eu.europa.ec.cipa.smp.server.security;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.math.BigInteger;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gutowpa on 04/04/2017.
 */
@ToString
@EqualsAndHashCode
public class PreAuthenticatedCertificatePrincipal implements Principal {

    private String certSerial;
    private String subjectDN;
    private String issuerDN;

    public PreAuthenticatedCertificatePrincipal(String subjectDN, String issuerDN, String certSerialNumber) {
        this.subjectDN = normalizeDN(subjectDN);
        this.issuerDN = normalizeDN(issuerDN);
        this.certSerial = normalizeSerial(certSerialNumber);
    }

    public PreAuthenticatedCertificatePrincipal(String subjectDN, String issuerDN, BigInteger certSerialNumber) {
        this(subjectDN, issuerDN, certSerialNumber.toString(16));
    }

    @Override
    public String getName() {
        return subjectDN + ":" + certSerial;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public String getIssuerDN() {
        return issuerDN;
    }

    private String normalizeSerial(String certSerial) {
        certSerial = certSerial.replaceAll(":", "");
        certSerial = certSerial.replaceFirst("0x", "");
        certSerial = StringUtils.leftPad(certSerial, 16, "0");
        return certSerial;
    }

    private String normalizeDN(String domainName) {
        LdapName ldapName;
        try {
            ldapName = new LdapName(domainName);
        } catch (InvalidNameException e) {
            throw new BadCredentialsException("Received invalid domain name for certificate pre-authentication: " + domainName, e);
        }

        // Make a map from type to name
        final Map<String, Rdn> parts = new HashMap<>();
        for (final Rdn rdn : ldapName.getRdns()) {
            parts.put(rdn.getType(), rdn);
        }

        // Keep always the same, reverse-importance order of CN, O, C
        StringBuilder dn = new StringBuilder();
        dn.append(parts.get("CN"));
        dn.append(",");
        dn.append(parts.get("O"));
        dn.append(",");
        dn.append(parts.get("C"));
        return dn.toString();
    }
}
