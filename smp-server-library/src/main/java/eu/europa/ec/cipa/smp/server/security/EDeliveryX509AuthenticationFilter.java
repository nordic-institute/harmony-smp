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

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

/**
 * @author Pawel Gutowski
 */
public class EDeliveryX509AuthenticationFilter extends X509AuthenticationFilter implements AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedCertificatePrincipal> {

    public EDeliveryX509AuthenticationFilter() {
        super.setAuthenticationDetailsSource(this);
        setPrincipalExtractor(new Extractor());
    }

    private static class Extractor implements X509PrincipalExtractor {
        @Override
        public Object extractPrincipal(X509Certificate cert) {
            String subject = cert.getSubjectDN().getName();
            String issuer = cert.getIssuerDN().getName();
            BigInteger serial = cert.getSerialNumber();

            return new PreAuthenticatedCertificatePrincipal(subject, issuer, serial);
        }
    }

    @Override
    public PreAuthenticatedCertificatePrincipal buildDetails(HttpServletRequest request) {
        PreAuthenticatedCertificatePrincipal principal = (PreAuthenticatedCertificatePrincipal) getPreAuthenticatedPrincipal(request);
        logger.info("Successfully extracted user details from X509 certificate: " + principal);
        return principal;
    }
}
