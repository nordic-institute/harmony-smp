/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.config.properties;

import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.WSSecurityConfigurerAdapter;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;


/**
 * Class update security configuration on property update event
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Component
public class SMPSecurityPropertyUpdateListener implements PropertyUpdateListener {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPSecurityPropertyUpdateListener.class);

    final WSSecurityConfigurerAdapter wsSecurityConfigurerAdapter;
    final ForwardedHeaderTransformer forwardedHeaderTransformer;

    public SMPSecurityPropertyUpdateListener(@Lazy WSSecurityConfigurerAdapter wsSecurityConfigurerAdapter,
                                             @Lazy ForwardedHeaderTransformer forwardedHeaderTransformer) {
        this.wsSecurityConfigurerAdapter = wsSecurityConfigurerAdapter;
        this.forwardedHeaderTransformer = forwardedHeaderTransformer;
    }

    @Override
    public void updateProperties(Map<SMPPropertyEnum, Object> properties) {
        setExternalTlsAuthenticationWithClientCertHeaderEnabled((Boolean) properties.get(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED));
        setExternalTlsAuthenticationWithX509CertificateHeaderEnabled((Boolean) properties.get(EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED));
        setForwardHeadersEnabled((Boolean) properties.get(HTTP_FORWARDED_HEADERS_ENABLED));
    }

    @Override
    public List<SMPPropertyEnum> handledProperties() {
        return Arrays.asList(
                EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED,
                EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED,
                HTTP_FORWARDED_HEADERS_ENABLED);
    }

    public void setExternalTlsAuthenticationWithClientCertHeaderEnabled(Boolean clientCertEnabled) {
        if (clientCertEnabled == null) {
            LOG.debug("Skip setting null client-cert");
            return;
        }
        LOG.info("Set Client-Cert headers  enabled: [{}]." , clientCertEnabled);
        if (clientCertEnabled) {
            LOG.warn("Set Client-Cert HTTP header enabled: [true]. Do not enable this option when using SMP without reverse-proxy and HTTP header protection!");
        }
        wsSecurityConfigurerAdapter.setExternalTlsAuthenticationWithClientCertHeaderEnabled(clientCertEnabled);
    }

    public void setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(Boolean clientCertEnabled) {
        if (clientCertEnabled == null) {
            LOG.debug("Skip setting null SSLClientCert");
            return;
        }
        LOG.info("Set SSLClientCert headers  enabled: [{}]." , clientCertEnabled);
        if (clientCertEnabled) {
            LOG.warn("Set SSLClientCert HTTP header enabled: [true]. Do not enable this option when using SMP without reverse-proxy and HTTP header protection!");
        }
        wsSecurityConfigurerAdapter.setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(clientCertEnabled);
    }

    public void setForwardHeadersEnabled(Boolean forwardHeadersEnabled) {
        if (forwardHeadersEnabled == null) {
            LOG.debug("Skip setting null Forward headers");
            return;
        }

        LOG.info("Set http forward headers  enabled: [{}]." , forwardHeadersEnabled);
        if (forwardHeadersEnabled) {
            LOG.warn("Set http forward headers  enabled:: [true]. Do not enable this option when using SMP without reverse-proxy and HTTP header protection!");
        }
        forwardedHeaderTransformer.setRemoveOnly(!forwardHeadersEnabled);
    }

}
