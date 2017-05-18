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
 * PreAuthenticated token and user details. To be used with {@code {@link BlueCoatAuthenticationFilter}}
 * and direct 2-way-SSL authentication, without reverse-proxy (not implemented yet).
 *
 * @author Pawel Gutowski
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
