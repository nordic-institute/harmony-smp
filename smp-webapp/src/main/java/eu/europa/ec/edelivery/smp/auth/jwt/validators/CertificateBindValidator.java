/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2025 - 2025 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.auth.jwt.validators;

import eu.europa.ec.edelivery.security.EDeliveryX509AuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * The CertBindValidator class validates attribute "cnf" in the JWT token.
 * The "cnf" (confirmation) claim is used to bind the token to a specific
 * key or certificate, enhancing security by ensuring that the token can only be used
 * by the entity possessing the corresponding key or certificate.
 * "cnf": { "x5t#S256": "bwcK0esc3ACC3DB2Y5_lESsXE8o9ltc05O89jdN-dg2" }  where x5t#S256 is the hash of the certificate.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
public class CertificateBindValidator extends NotEmptyClaimValidator {
    private static final Logger LOG = LoggerFactory.getLogger(CertificateBindValidator.class);
    // the cnf claim is defined in RFC 7800
    private static final String CLAIM_NAME = "cnf";
    // the x5t#S256 certificate thumbprint is defined in RFC 7515
    private static final String CLAIM_X5T_S256 = "x5t#S256";
    private static final String ERROR_MESSAGE_URI = "https://datatracker.ietf.org/doc/html/rfc8705";

    public CertificateBindValidator() {
        super(CLAIM_NAME);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        super.validateHasClaim(token, ERROR_MESSAGE_URI);

        // get X5t#S256 from cnf claim
        Map<String, Object> cnfClaim = token.getClaimAsMap(this.claimName);
        if (cnfClaim == null || cnfClaim.get(CLAIM_X5T_S256) == null) {
            return OAuth2TokenValidatorResult
                    .failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Claim: [" + this.claimName + "] must have property: [" + CLAIM_X5T_S256 + "]",
                            ERROR_MESSAGE_URI));

        }
        Object x5tS256 = cnfClaim.get(CLAIM_X5T_S256);
        if (x5tS256 instanceof String certThumbprint) {
            if (Strings.CS.equals(certThumbprint, getMTLSCertificateHash())) {
                return OAuth2TokenValidatorResult.success();
            } else {
                return OAuth2TokenValidatorResult
                        .failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Claim: [" + this.claimName + "] property: [" + CLAIM_X5T_S256 + "] does not match",
                                ERROR_MESSAGE_URI));
            }
        } else {
            return OAuth2TokenValidatorResult
                    .failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Claim: [" + this.claimName + "] property: [" + CLAIM_X5T_S256 + "] must be of type String",
                            ERROR_MESSAGE_URI));
        }
    }

    private static String getMTLSCertificateHash() {
        X509Certificate certificate;
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        // Try to get certificate from attribute first (as set by the servlet container)
        certificate = getCertificateFromAttribute(EDeliveryX509AuthenticationFilter.HTTP_ATTRIBUTE_JAVAX_CERTIFICATE, request);
        if (certificate == null) {
            // Try to get certificate from another header (as set by a reverse proxy)
            certificate = getCertificateFromHeader(EDeliveryX509AuthenticationFilter.HTTP_HEADER_RP_CERTIFICATE, request);
        }
        if (certificate == null) {
            LOG.warn("No client certificate found in the request.");
            return null;
        }

        try {
            byte[] encodedCert = certificate.getEncoded();
            // Calculate the SHA-256 hash of the certificate
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(encodedCert);
            // Encode the hash using Base64 URL encoding without padding
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            LOG.error("Error calculating certificate hash: {}", ExceptionUtils.getRootCauseMessage(e));
            return null;
        }



    }


    public static X509Certificate getCertificateFromHeader(String name, HttpServletRequest request) {
        LOG.debug("Get X509 certificate from header [{}]", name);
        CertificateFactory cf;
        String headerValue = null;
        try {
            headerValue = request.getHeader(name);
            if (isBlank(headerValue)) {
                LOG.debug("Can not parse X509 certificate from header [{}]: [{}] with error: [Header value is empty!].", name, headerValue);
                return null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(headerValue));
            cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(bais);
        } catch (CertificateException e) {
            LOG.error("Can not parse X509 certificate from header [{}]: [{}] with error: [{}].", name, headerValue, ExceptionUtils.getRootCauseMessage(e));
        } catch (RuntimeException ex) {
            LOG.error("Can not decode base64  header [{}]: [{}] with error: [{}].", name, headerValue, ExceptionUtils.getRootCauseMessage(ex));
        }
        return null;
    }

    public static X509Certificate getCertificateFromAttribute(String name, HttpServletRequest request) {
        LOG.debug("Get X509 certificate from attribute [{}]", name);

        X509Certificate[] result = (X509Certificate[]) request.getAttribute(name);
        if (result != null && result.length > 0) {
            return result[0];
        }
        return null;
    }

}
